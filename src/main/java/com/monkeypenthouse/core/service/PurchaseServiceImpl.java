package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.component.CacheManager;
import com.monkeypenthouse.core.component.OrderIdGenerator;
import com.monkeypenthouse.core.connect.TossPaymentsConnector;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.*;
import com.monkeypenthouse.core.repository.dto.PurchaseTicketMappingDto;
import com.monkeypenthouse.core.repository.entity.*;
import com.monkeypenthouse.core.service.dto.purchase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final AmenityRepository amenityRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseTicketMappingRepository purchaseTicketMappingRepository;
    private final TicketRepository ticketRepository;
    private final TicketStockRepository ticketStockRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final UserService userService;
    private final CacheManager cacheManager;
    private final DataSourceTransactionManager txManager;

    private final TossPaymentsConnector tossPaymentsConnector;



    @Override
    public PurchaseCreateResS createPurchase(final UserDetails userDetails, final PurchaseCreateReqS params) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus sts = txManager.getTransaction(def);

        final Amenity amenity = amenityRepository.findById(
                params.getPurchaseTicketMappingDtoList().get(0).getAmenityId())
                .orElseThrow(() -> new CommonException(ResponseCode.DATA_NOT_FOUND));

        if (amenity.getStatus() != 0) {
            throw new CommonException(ResponseCode.PURCHASE_UNABLE_AMENITY);
        }

        /**
         * Step 1. 티켓 리스트 DB 조회
         */

        final List<Ticket> ticketList;
        try {

            // 티켓 엔티티 리스트 검증 및 조회
            ticketList = (List<Ticket>) ticketRepository.findAllById(
                    params.getPurchaseTicketMappingDtoList().stream()
                            .map(e -> e.getTicketId())
                            .collect(Collectors.toList()));

            if (ticketList.size() != params.getPurchaseTicketMappingDtoList().size()) {
                throw new CommonException(ResponseCode.TICKET_NOT_FOUND);
            }

            /**
             * Step 2. 티켓 재고 수량 체크
             */

            // 티켓 재고 수량 검증
            for (final PurchaseTicketMappingS vo : params.getPurchaseTicketMappingDtoList()) {
                if (
                        cacheManager.getTotalQuantityOfTicket(vo.getTicketId()) <
                                cacheManager.getPurchasedQuantityOfTicket(vo.getTicketId()) + vo.getQuantity()
                ) {
                    throw new CommonException(ResponseCode.NOT_ENOUGH_TICKETS);
                }
            }

            /**
             * Step 3. 총 주문 금액, 주문 번호, 주문 이름 생성
             */

            // 티켓 ID : 구매 개수 HashMap 구성
            final HashMap<Long, Integer> quantityMap = new HashMap<>();

            params.getPurchaseTicketMappingDtoList().forEach(
                    e -> quantityMap.put(e.getTicketId(), e.getQuantity()));

            // amount 측정
            final int amount = ticketList.stream()
                    .mapToInt(t -> quantityMap.get(t.getId()) * t.getPrice())
                    .sum();

            // orderId 생성
            final String orderId = orderIdGenerator.generate();

            // Redis에 orderId: map{ticketId: ticketQuantity}
            cacheManager.setTicketInfoOfPurchase(
                    orderId,
                    params.getPurchaseTicketMappingDtoList()
                            .stream()
                            .map(e -> new PurchaseTicketMappingDto(e.getAmenityId(), e.getTicketId(), e.getQuantity()))
                            .collect(Collectors.toList())
            );

            // Redis에 ticketId:amenityId 저장
            params.getPurchaseTicketMappingDtoList()
                    .forEach(vo -> cacheManager.setAmenityIdOfTicket(vo.getTicketId(), vo.getAmenityId()));

            // orderName 생성
            String orderName = ticketList.get(0).getName();
            if (ticketList.size() > 1) {
                orderName += " 외 " + (ticketList.size() - 1) + "건";
            }

            /**
             * Step 4. Purchase 및 PurchaseTicketMapping 엔티티 생성 후 return
             */

            // Purchase 엔티티 생성
            final User user = userService.getUserByEmail(userDetails.getUsername());

            final Purchase purchase = new Purchase(user, orderId, orderName, amount, OrderStatus.IN_PROGRESS, );
            purchaseRepository.save(purchase);

            // PurchaseTicketMapping 엔티티 생성
            ticketList.forEach(ticket ->
                    purchaseTicketMappingRepository.save(new PurchaseTicketMapping(purchase, ticket, quantityMap.get(ticket.getId())))
            );
            txManager.commit(sts);

            return PurchaseCreateResS.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .orderName(orderName)
                    .build();

        } catch (Exception e) {
            txManager.rollback(sts);
            throw e;
        }
    }

    @Override
    public void approvePurchase(final PurchaseApproveReqS params) throws IOException, InterruptedException {

        /**
         * Step 1. orderId 로부터 티켓 정보 불러오기
         */
        final Map<Long, Long> ticketInfo = cacheManager.getTicketInfoOfPurchase(params.getOrderId());
        if (ticketInfo.isEmpty()) {
            throw new CommonException(ResponseCode.ORDER_NOT_FOUND);
        }

        /**
         * Step 2. Multi Lock 획득 & 트랜잭션 시작
         */
        CacheManager.LockWithTimeOut lockWithTimeOut = cacheManager.tryMultiLockOnPurchasedQuantityOfTicket(new ArrayList<>(ticketInfo.keySet()));

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus sts = txManager.getTransaction(def);

        try {
            /**
             * Step 3. 티켓 재고 수량 체크
             */

            // 티켓 재고 수량 검증
            for (Long ticketId : ticketInfo.keySet()) {
                Long ticketQuantity = ticketInfo.get(ticketId);

                if (
                        cacheManager.getTotalQuantityOfTicket(ticketId) <
                                cacheManager.getPurchasedQuantityOfTicket(ticketId) + ticketQuantity
                ) {
                    throw new CommonException(ResponseCode.NOT_ENOUGH_TICKETS);
                }
            }

            /**
             * Step 4. orderId로부터 Purchase 엔티티 조회
             */
            final Purchase purchase = purchaseRepository.findByOrderId(params.getOrderId())
                    .orElseThrow(() -> new CommonException(ResponseCode.ORDER_NOT_FOUND));

            /**
             * Step 5. tossPayments API 호출
             */
            tossPaymentsConnector.approvePayments(
                    params.getPaymentKey(),
                    params.getAmount(),
                    params.getOrderId()
            );

            purchase.changeOrderStatus(OrderStatus.COMPLETED);
            purchase.setPaymentsKey(params.getPaymentKey());

            /**
             * Step 6. tossPayments API 승인 시 Redis / DB 업데이트
             */
            for (Long ticketId : ticketInfo.keySet()) {
                Long ticketQuantity = ticketInfo.get(ticketId);
                Long amenityId = cacheManager.getAmenityIdOfTicket(ticketId);

                // DB 업데이트
                TicketStock ticketStock = ticketStockRepository.findById(ticketId)
                        .orElseThrow(() -> new CommonException(ResponseCode.TICKET_NOT_FOUND));

                ticketStock.increasePurchasedQuantity(ticketQuantity);

                // Redis 업데이트
                int newPurchasedQuantity = cacheManager.getPurchasedQuantityOfTicket(ticketId) + ticketQuantity;
                cacheManager.setPurchasedQuantityOfTicket(ticketId, newPurchasedQuantity);

                int newAmenityQuantity = cacheManager.getPurchasedQuantityOfAmenity(amenityId) + ticketQuantity;
                cacheManager.setPurchasedQuantityOfAmenity(amenityId, newAmenityQuantity);
            }

            if (System.currentTimeMillis() < lockWithTimeOut.getTimeOut()) {
                txManager.commit(sts);
            } else {
                throw new CommonException(ResponseCode.DATA_EXECUTION_TIME_TOO_LONG);
            }
        } catch (Exception e) {
            txManager.rollback(sts);
            throw e;
        } finally {
            cacheManager.unlock(lockWithTimeOut.getRLock());
            cacheManager.removeTicketInfoOfPurchase(params.getOrderId());
        }
    }

    @Override
    @Transactional
    public void cancelPurchase(final PurchaseCancelReqS params) {
        final Purchase purchase =
                purchaseRepository.findByOrderId(params.getOrderId())
                        .orElseThrow(() -> new CommonException(ResponseCode.ORDER_NOT_FOUND));

        if (purchase.getOrderStatus()!=OrderStatus.IN_PROGRESS) {
            throw new CommonException(ResponseCode.CANCEL_NOT_ENABLE);
        }

        purchase.changeOrderStatus(OrderStatus.CANCELLED);
        cacheManager.removeTicketInfoOfPurchase(params.getOrderId());
    }

    @Override
    public void refundPurchase(PurchaseRefundReqS params) throws IOException, InterruptedException {
        // 유효성 검사 1. 주문 정보 유효성 검사
        final Purchase purchase = purchaseRepository.findById(params.getPurchaseId())
                .orElseThrow(() -> new CommonException(ResponseCode.ORDER_NOT_FOUND));

        if (purchase.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new CommonException(ResponseCode.CANCEL_NOT_ENABLE);
        }

        // 유효성 검사 2. 어메니티 정보 유효성 검사
        final Amenity amenity = amenityRepository.findById(params.getAmenityId())
                .orElseThrow(() -> new CommonException(ResponseCode.DATA_NOT_FOUND));

        if (amenity.getStatus() != 0) {
            throw new CommonException(ResponseCode.CANCEL_UNABLE_AMENITY);
        }

        // 2. tosspayments 취소 요청
        tossPaymentsConnector.refundPayments(purchase.getPaymentsKey(), CancelReason.CHANGE_OF_MIND);

        // 3. purchase 정보 수정
        purchase.setCancelReason(CancelReason.CHANGE_OF_MIND);

        // 4. 재고 관리
        purchase.getPurchaseTicketMappingList().forEach(
                e -> cacheManager.addPurchasedQuantityOfTicket(e.getTicket().getId(), e.getQuantity())
        );

        int totalAmount = purchase.getPurchaseTicketMappingList()
                .stream().mapToInt(PurchaseTicketMapping::getQuantity).sum();

        cacheManager.add




    }
}
