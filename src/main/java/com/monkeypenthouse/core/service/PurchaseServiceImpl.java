package com.monkeypenthouse.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkeypenthouse.core.component.OrderIdGenerator;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.tossPayments.ApprovePaymentResponseDto;
import com.monkeypenthouse.core.entity.*;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.*;
import com.monkeypenthouse.core.vo.*;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseTicketMappingRepository purchaseTicketMappingRepository;
    private final TicketRepository ticketRepository;
    private final AmenityRepository amenityRepository;
    private final TicketStockRepository ticketStockRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final UserService userService;
    private final RedissonClient redissonClient;

    @Value("${toss-payments.api-key}")
    private String tossPaymentsApiKey;

    @Override
    @Transactional
    public CreateOrderResponseVo createPurchase(final UserDetails userDetails, final CreatePurchaseRequestVo requestVo) {
        /**
         * Step 1. 티켓 리스트 DB 조회
         */

        // 티켓 엔티티 리스트 검증 및 조회
        final List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAllById(
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
                    (Integer) redissonClient.getBucket(vo.getTicketId() + ":totalQuantity").get() <
                            (Integer) redissonClient.getBucket(vo.getTicketId() + ":purchasedQuantity").get() +
                                    vo.getQuantity()
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

        // Redis에 orderId:ticketIdSet, orderId: ticketQuantity 저장
        RList<Long> ticketIdList = redissonClient.getList(orderId + ":ticketIds");
        RList<Integer> ticketQuantityList = redissonClient.getList(orderId + ":ticketQuantity");

        for (PurchaseTicketMappingVo vo : requestVo.getPurchaseTicketMappingVoList()) {
            ticketIdList.add(vo.getTicketId());
            ticketQuantityList.add(vo.getQuantity());
        }

        // Redis에 ticketId:amenityId 저장
        requestVo.getPurchaseTicketMappingVoList()
                .forEach(vo -> redissonClient
                                .getBucket(vo.getTicketId() + ":amenityId")
                                .set(vo.getAmenityId()));

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
        ticketList.stream().map(ticket ->
                purchaseTicketMappingRepository.save(new com.monkeypenthouse.core.entity.PurchaseTicketMapping(purchase, ticket, quantityMap.get(ticket.getId())))
        );

        return CreateOrderResponseVo.builder()
                .amount(amount)
                .orderId(orderId)
                .orderName(orderName)
                .build();
    }

    @Override
    @Transactional
    public void approvePurchase(final ApproveOrderRequestVo requestVo) throws IOException, InterruptedException {

        /**
         * Step 1. orderId 로부터 티켓 정보 불러오기
         */
        RList<Long> ticketIdList = redissonClient.getList(requestVo.getOrderId() + ":ticketIds");
        RList<Integer> ticketQuantityList = redissonClient.getList(requestVo.getOrderId() + ":ticketQuantity");

        final List<Long> ticketIds = ticketIdList.readAll();
        final List<Integer> ticketQuantities = ticketQuantityList.readAll();

        /**
         * Step 2. Multi Lock 획득
         */
////      key {ticketId}:purchasedQuantity 에 대한 멀티 락 객체 생성
//        final RLock multiLock = redissonClient.getMultiLock(
//                ticketIds.stream().map(
//                                ticketId -> redissonClient.getLock(ticketId + ":purchasedQuantity"))
//                        .collect(Collectors.toList())
//                        .toArray(RLock[]::new));
//
////      멀티 락 시도
//        final boolean isLocked = multiLock.tryLock();
//
//        if (!isLocked) {
//            throw new CommonException(ResponseCode.TICKET_LOCK_FAILED);
//        }

        try {
            /**
             * Step 3. 티켓 재고 수량 체크
             */

            // 티켓 재고 수량 검증
            for (int i = 0; i < ticketIds.size(); i++) {
                Long ticketId = ticketIds.get(i);
                Integer ticketQuantity = ticketQuantities.get(i);
                System.out.println(ticketId + ":totalQuantity = " + redissonClient.getBucket(ticketId + ":totalQuantity").get());
                System.out.println(ticketId + ":purchasedQuantity = " + redissonClient.getBucket(ticketId + ":purchasedQuantity").get());

                if (
                        (Integer) redissonClient.getBucket(ticketId + ":totalQuantity").get() <
                                (Integer) redissonClient.getBucket(ticketId + ":purchasedQuantity").get() +
                                        ticketQuantity
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

//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://api.tosspayments.com/v1/payments/" + requestVo.getPaymentKey()))
//                    .header("Authorization", tossPaymentsApiKey)
//                    .header("Content-Type", "application/json")
//                    .method("POST", HttpRequest.BodyPublishers.ofString(
//                            "{\"amount\":" + requestVo.getAmount() +
//                                    ",\"orderId\":\"" + requestVo.getOrderId() + "\"}"))
//                    .build();

//            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

//            final ApprovePaymentResponseDto responseDto = objectMapper.readValue(response.body(), ApprovePaymentResponseDto.class);

//            if (response.statusCode() == 200) {
            purchase.changeOrderStatus(OrderStatus.COMPLETED);

            /**
             * Step 6. tossPayments API 승인 시 Redis / DB 업데이트
             */

            for (int i = 0; i < ticketIds.size(); i++) {
                Long ticketId = ticketIds.get(i);
                Integer ticketQuantity = ticketQuantities.get(i);
                Long amenityId = (Long) redissonClient.getBucket(ticketId + ":amenityId").get();

                // Redis 업데이트
                Integer newPurchasedQuantity =
                        (Integer) redissonClient.getBucket(ticketId + ":purchasedQuantity").get() + ticketQuantity;
                redissonClient.getBucket(ticketId + ":purchasedQuantity").set(newPurchasedQuantity);

                Integer newAmenityQuantity = (Integer) redissonClient.getBucket(amenityId + ":purchasedQuantityOfTickets").get() + ticketQuantity;
                redissonClient.getBucket(amenityId + ":purchasedQuantityOfTickets")
                        .set(newAmenityQuantity);

                // DB 업데이트
                TicketStock ticketStock = ticketStockRepository.findById(ticketId)
                        .orElseThrow(() -> new CommonException(ResponseCode.TICKET_NOT_FOUND));

                ticketStock.reduce(ticketQuantity);
            }

//            } else {
//                throw new CommonException(ResponseCode.ORDER_PAYMENT_NOT_APPROVED);
//            }
        } catch (Exception e) {
            // 스레드가 Interrupt 되었을 때 예외 처리
            throw e;
        } finally {
//            System.out.println("락 해제");
//            multiLock.unlock();
        }
    }


    // 추후에 이로직은 서버 외부에서 이벤트 요청에 의해 수행되어야함
    @Override
    @PostConstruct
    @Transactional(readOnly = true)
    public void loadPurchaseDataOnRedis() {

        final List<Amenity> amenityList = amenityRepository.findAllWithTicketsUsingFetchJoin();

        for (Amenity amenity : amenityList) {
            Set<Long> ticketIds = amenity.getTickets().stream().map(t -> t.getId()).collect(Collectors.toSet());
            List<TicketStock> ticketStocks = ticketStockRepository.findAllByTicketIdIn(ticketIds);

            redissonClient.getBucket(amenity.getId() + ":totalQuantityOfTickets")
                    .set(ticketStocks.stream().mapToInt(TicketStock::getTotalQuantity).sum());
            redissonClient.getBucket(amenity.getId() + ":purchasedQuantityOfTickets")
                    .set(ticketStocks.stream().mapToInt(TicketStock::getPurchasedQuantity).sum());

            for (TicketStock ticketStock : ticketStocks) {
                redissonClient.getBucket(ticketStock.getTicketId()+":totalQuantity")
                        .set(ticketStock.getTotalQuantity());
                redissonClient.getBucket(ticketStock.getTicketId()+":purchasedQuantity")
                        .set(ticketStock.getPurchasedQuantity());
            }
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
    }
}
