package com.monkeypenthouse.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkeypenthouse.core.component.OrderIdGenerator;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.tossPayments.ApprovePaymentResponseDto;
import com.monkeypenthouse.core.entity.*;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.PurchaseTicketMappingRepository;
import com.monkeypenthouse.core.repository.PurchaseRepository;
import com.monkeypenthouse.core.repository.TicketRepository;
import com.monkeypenthouse.core.vo.ApproveOrderRequestVo;
import com.monkeypenthouse.core.vo.CreatePurchaseRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import com.monkeypenthouse.core.vo.PurchaseTicketMappingVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseTicketMappingRepository purchaseTicketMappingRepository;
    private final TicketRepository ticketRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final RedisTemplate redisTemplate;

    @Value("${toss-payments.api-key}")
    private String tossPaymentsApiKey;

    @Override
    @Transactional
    public CreateOrderResponseVo createPurchase(final UserDetails userDetails, final CreatePurchaseRequestVo requestVo) {

        /**
         * Step 1. (Redis 데이터 존재 여부 체크)
         */

        // ticketList로 collect
        final List<Long> ticketIdList = requestVo.getPurchaseTicketMappingVoList().stream()
                .map(purchaseTicketMappingVo -> purchaseTicketMappingVo.getTicketId())
                .collect(Collectors.toList());

        /**
         * Step 2. 티켓 리스트 DB 조회
         */

        // 티켓 엔티티 리스트 검증 및 조회
        final List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAllById(ticketIdList);

        if (ticketList.size() != requestVo.getPurchaseTicketMappingVoList().size()) {
            throw new CommonException(ResponseCode.TICKET_NOT_FOUND);
        }

        /**
         * Step 3. 티켓 재고 수량 체크
         */

        // 티켓 재고 수량 검증
        for (final PurchaseTicketMappingVo vo : requestVo.getPurchaseTicketMappingVoList()) {
            if (
                    (int) redissonClient.getBucket(vo.getTicketId() + ":totalCapacity").get() <
                            (int) redissonClient.getBucket(vo.getTicketId() + ":purchasedCapacity").get() +
                                    vo.getQuantity()
            ) {
                throw new CommonException(ResponseCode.NOT_ENOUGH_TICKETS);
            }
        }

        /**
         * Step 4. 총 주문 금액, 주문 번호, 주문 이름 생성
         */

        // 티켓 ID : 구매 개수 HashMap 구성
        final HashMap<Long, Integer> quantityMap = new HashMap<>();

        requestVo.getPurchaseTicketMappingVoList().stream().map(
                purchaseTicketMappingVo -> quantityMap.put(purchaseTicketMappingVo.getTicketId(), purchaseTicketMappingVo.getQuantity()));

        // amount 측정
        final int amount = ticketList.stream()
                .mapToInt(t -> quantityMap.get(t.getId()) * t.getPrice())
                .sum();

        // orderId 생성
        final String orderId = orderIdGenerator.generate();

        // Redis에 orderId:ticketIdSet, orderId: ticketQuantity 저장
        ListOperations<String, Long> listOperations = redisTemplate.opsForList();

        for (PurchaseTicketMappingVo vo : requestVo.getPurchaseTicketMappingVoList()) {
            listOperations.rightPush(orderId + ":ticketIds", vo.getTicketId());
            listOperations.rightPush(orderId + ":ticketQuantity", Long.valueOf(vo.getQuantity()));
        }

        // Redis에 ticketId:amenityId 저장
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        requestVo.getPurchaseTicketMappingVoList()
                .forEach(vo -> valueOperations.set(vo.getTicketId() + ":amenityId", vo.getAmenityId()));

        // orderName 생성
        String orderName = ticketList.get(0).getName();
        if (ticketList.size() > 1) {
            orderName += " 외 " + (ticketList.size() - 1) + "건";
        }

        /**
         * Step 5. Purchase 및 PurchaseTicketMapping 엔티티 생성 후 return
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
         * Step 1. (Redis 데이터 존재 여부 체크)
         */

        /**
         * Step 2. orderId 로부터 티켓 정보 불러오기
         */

        ListOperations<String, Long> listOperations = redisTemplate.opsForList();

        final List<Long> ticketIds = listOperations.range(requestVo.getOrderId() + ":ticketIds", 0, -1);
        final List<Long> ticketQuantities = listOperations.range(requestVo.getOrderId() + ":ticketQuantity", 0, -1);

        /**
         * Step 3. Multi Lock 획득
         */
        // key {ticketId}:purchasedCapacity 에 대한 멀티 락 객체 생성
        final RLock multiLock = redissonClient.getMultiLock(
                ticketIds.stream().map(
                                ticketId -> redissonClient.getLock(ticketId + "purchasedCapacity"))
                        .collect(Collectors.toList())
                        .toArray(RLock[]::new));

        try {
            // 멀티 락 시도
            final boolean isLocked = multiLock.tryLock(2, 1, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new CommonException(ResponseCode.TICKET_LOCK_FAILED);
            }

            /**
             * Step 4. 티켓 재고 수량 체크
             */

            // 티켓 재고 수량 검증
            for (int i = 0; i < ticketIds.size(); i++) {
                Long ticketId = ticketIds.get(i);
                Long ticketQuantity = ticketQuantities.get(i);

                if (
                        (int) redissonClient.getBucket(ticketId + ":totalCapacity").get() <
                                (int) redissonClient.getBucket(ticketId + ":purchasedCapacity").get() +
                                        ticketQuantity
                ) {
                    throw new CommonException(ResponseCode.NOT_ENOUGH_TICKETS);
                }
            }

            /**
             * Step 5. orderId로부터 Purchase 엔티티 조회
             */

            final Purchase purchase = purchaseRepository.findByOrderId(requestVo.getOrderId())
                    .orElseThrow(() -> new CommonException(ResponseCode.ORDER_NOT_FOUND));

            /**
             * Step 6. tossPayments API 호출
             */

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/" + requestVo.getPaymentKey()))
                    .header("Authorization", tossPaymentsApiKey)
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(
                            "{\"amount\":" + requestVo.getAmount() +
                                    ",\"orderId\":\"" + requestVo.getOrderId() + "\"}"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            final ApprovePaymentResponseDto responseDto = objectMapper.readValue(response.body(), ApprovePaymentResponseDto.class);

            ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();

            if (response.statusCode() == 200) {
                purchase.changeOrderStatus(OrderStatus.COMPLETED);
                // Redis 업데이트
                for (int i = 0; i < ticketIds.size(); i++) {
                    Long ticketId = ticketIds.get(i);
                    Long ticketQuantity = ticketQuantities.get(i);
                    Long amenityId = valueOperations.get(ticketId + ":amenityId");

                    Long newPurchasedQuantity =
                            (Long) redissonClient.getBucket(ticketId + ":purchasedCapacity").get() + ticketQuantity;
                    redissonClient.getBucket(ticketId + ":purchasedCapacity").set(newPurchasedQuantity);

                    Long newAmenityQuantity = valueOperations.get(amenityId + ":purchasedCapacityOfTickets") + ticketQuantity;
                    valueOperations.set(amenityId + ":purchasedCapacityOfTickets", newAmenityQuantity);
                }

                // DB 업데이트

            } else {
                throw new CommonException(ResponseCode.ORDER_PAYMENT_NOT_APPROVED);
            }

        } catch (Exception e) {
            // 스레드가 Interrupt 되었을 때 예외 처리

        } finally {
            /**
             * Step 6. Multi Lock 해제
             */

            multiLock.unlock();
        }
    }
}
