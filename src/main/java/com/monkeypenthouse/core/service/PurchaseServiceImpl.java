package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.component.CacheManager;
import com.monkeypenthouse.core.component.OrderIdGenerator;
import com.monkeypenthouse.core.connect.TossPaymentsConnector;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.tossPayments.ApprovePaymentResponseDto;
import com.monkeypenthouse.core.entity.*;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.*;
import com.monkeypenthouse.core.vo.*;
import lombok.RequiredArgsConstructor;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
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
    public CreateOrderResponseVo createPurchase(final UserDetails userDetails, final CreatePurchaseRequestVo requestVo) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus sts = txManager.getTransaction(def);

        /**
         * Step 1. 티켓 리스트 DB 조회
         */

        final List<Ticket> ticketList;
        try {

            // 티켓 엔티티 리스트 검증 및 조회
            ticketList = (List<Ticket>) ticketRepository.findAllById(
                    requestVo.getPurchaseTicketMappingVoList().stream()
                            .map(purchaseTicketMappingVo -> purchaseTicketMappingVo.getTicketId())
                            .collect(Collectors.toList()));

            if (ticketList.size() != requestVo.getPurchaseTicketMappingVoList().size()) {
                throw new CommonException(ResponseCode.TICKET_NOT_FOUND);
            }

            /**
             * Step 2. 티켓 재고 수량 체크
             */

            // 티켓 재고 수량 검증
            for (final PurchaseTicketMappingVo vo : requestVo.getPurchaseTicketMappingVoList()) {
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

            requestVo.getPurchaseTicketMappingVoList().forEach(
                    purchaseTicketMappingVo -> quantityMap.put(purchaseTicketMappingVo.getTicketId(), purchaseTicketMappingVo.getQuantity()));

            // amount 측정
            final int amount = ticketList.stream()
                    .mapToInt(t -> quantityMap.get(t.getId()) * t.getPrice())
                    .sum();

            // orderId 생성
            final String orderId = orderIdGenerator.generate();

            // Redis에 orderId: map{ticketId: ticketQuantity}
            cacheManager.setTicketInfoOfPurchase(orderId, requestVo.getPurchaseTicketMappingVoList());

            // Redis에 ticketId:amenityId 저장
            requestVo.getPurchaseTicketMappingVoList()
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

            final Purchase purchase = new Purchase(user, orderId, orderName, amount, OrderStatus.IN_PROGRESS);
            purchaseRepository.save(purchase);

            // PurchaseTicketMapping 엔티티 생성
            ticketList.forEach(ticket ->
                    purchaseTicketMappingRepository.save(new PurchaseTicketMapping(purchase, ticket, quantityMap.get(ticket.getId())))
            );
            txManager.commit(sts);

            return CreateOrderResponseVo.builder()
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
    public void approvePurchase(final ApproveOrderRequestVo requestVo) throws IOException, InterruptedException {

        /**
         * Step 1. orderId 로부터 티켓 정보 불러오기
         */
        final Map<Long, Integer> ticketInfo = cacheManager.getTicketInfoOfPurchase(requestVo.getOrderId());
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
                Integer ticketQuantity = ticketInfo.get(ticketId);

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
            final Purchase purchase = purchaseRepository.findByOrderId(requestVo.getOrderId())
                    .orElseThrow(() -> new CommonException(ResponseCode.ORDER_NOT_FOUND));

            /**
             * Step 5. tossPayments API 호출
             */
            tossPaymentsConnector.approvePayments(
                    requestVo.getPaymentKey(),
                    requestVo.getAmount(),
                    requestVo.getOrderId()
            );

            purchase.changeOrderStatus(OrderStatus.COMPLETED);

            /**
             * Step 6. tossPayments API 승인 시 Redis / DB 업데이트
             */
            for (Long ticketId : ticketInfo.keySet()) {
                Integer ticketQuantity = ticketInfo.get(ticketId);
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
            cacheManager.removeTicketInfoOfPurchase(requestVo.getOrderId());
        }
    }

    @Override
    @Transactional
    public void cancelPurchase(final CancelPurchaseRequestVo requestVo) {
        final Purchase purchase =
                purchaseRepository.findByOrderId(requestVo.getOrderId()).orElseThrow(() -> new CommonException(ResponseCode.ORDER_NOT_FOUND));

        if (purchase.getOrderStatus()!=OrderStatus.IN_PROGRESS) {
            throw new CommonException(ResponseCode.CANCEL_NOT_ENABLE);
        }

        purchase.changeOrderStatus(OrderStatus.CANCELLED);
        cacheManager.removeTicketInfoOfPurchase(requestVo.getOrderId());
    }
}
