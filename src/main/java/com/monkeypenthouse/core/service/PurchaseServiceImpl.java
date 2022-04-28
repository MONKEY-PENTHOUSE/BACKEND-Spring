package com.monkeypenthouse.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkeypenthouse.core.component.OrderIdGenerator;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.NaverUserDTO;
import com.monkeypenthouse.core.dto.tossPayments.ApprovePaymentResponseDto;
import com.monkeypenthouse.core.entity.*;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.PurchaseTicketMappingRepository;
import com.monkeypenthouse.core.repository.PurchaseRepository;
import com.monkeypenthouse.core.repository.TicketRepository;
import com.monkeypenthouse.core.vo.ApproveOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import com.monkeypenthouse.core.vo.OrderProductVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${toss-payments.api-key}")
    private String tossPaymentsApiKey;

    @Override
    @Transactional
    public CreateOrderResponseVo createPurchase(final UserDetails userDetails, final CreateOrderRequestVo requestVo) {

        /**
         * Step 1. (Redis 데이터 존재 여부 체크)
         */

        /**
         * Step 2. Multi Lock 획득
         */

        // ticketList로 collect
        final List<Long> ticketIdList = requestVo.getOrderProductVoList().stream()
                .map(orderProductVo -> orderProductVo.getTicketId())
                .collect(Collectors.toList());

        // key {ticketId}:purchasedCapacity 에 대한 멀티 락 객체 생성
        final RLock multiLock = redissonClient.getMultiLock(
                ticketIdList.stream().map(
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
             * Step 3. 티켓 리스트 DB 조회
             */

            // 티켓 엔티티 리스트 검증 및 조회
            final List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAllById(ticketIdList);

            if (ticketList.size() != requestVo.getOrderProductVoList().size()) {
                throw new CommonException(ResponseCode.TICKET_NOT_FOUND);
            }

            /**
             * Step 4. 티켓 재고 수량 체크
             */

            // 티켓 재고 수량 검증
            for (final OrderProductVo vo : requestVo.getOrderProductVoList()) {
                if (
                        (int) redissonClient.getBucket(vo.getTicketId() + "totalCapacity").get() <
                                (int) redissonClient.getBucket(vo.getTicketId() + "purchasedCapacity").get() +
                                        vo.getQuantity()
                ) {
                    throw new CommonException(ResponseCode.NOT_ENOUGH_TICKETS);
                }
            }

            /**
             * Step 5. 총 주문 금액, 주문 번호, 주문 이름 생성
             */

            // 티켓 ID : 구매 개수 HashMap 구성
            final HashMap<Long, Integer> quantityMap = new HashMap<>();

            requestVo.getOrderProductVoList().stream().map(
                    orderProductVo -> quantityMap.put(orderProductVo.getTicketId(), orderProductVo.getQuantity()));

            // amount 측정
            final int amount = ticketList.stream()
                    .mapToInt(t -> quantityMap.get(t.getId()) * t.getPrice())
                    .sum();

            // orderId 생성
            final String orderId = orderIdGenerator.generate();

            // orderName 생성
            String orderName = ticketList.get(0).getName();
            if (ticketList.size() > 1) {
                orderName += " 외 " + (ticketList.size() - 1) + "건";
            }

            /**
             * Step 6. Purchase 및 PurchaseTicketMapping 엔티티 생성 후 return
             */

            // Purchase 엔티티 생성
            final User user = userService.getUserByEmail(userDetails.getUsername());

            final Purchase purchase = new Purchase(user, orderId, orderName, amount, OrderStatus.IN_PROGRESS);
            purchaseRepository.save(purchase);

            // PurchaseTicketMapping 엔티티 생성
            ticketList.stream().map(ticket ->
                    purchaseTicketMappingRepository.save(new PurchaseTicketMapping(purchase, ticket, quantityMap.get(ticket.getId())))
            );

            return CreateOrderResponseVo.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .orderName(orderName)
                    .build();

        } catch (Exception e) {
            // 스레드가 Interrupt 되었을 때 예외 처리

        } finally {
            /**
             * Step 7. Multi Lock 해제
             */

            multiLock.unlock();
        }


        throw new CommonException(ResponseCode.ORDER_CREATE_FAILED);
    }

    @Override
    @Transactional
    public void approvePurchase(final ApproveOrderRequestVo requestVo) throws IOException, InterruptedException {

        final Purchase purchase = purchaseRepository.findByOrderId(requestVo.getOrderId())
                .orElseThrow(() -> new CommonException(ResponseCode.ORDER_NOT_FOUND));

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

        if (response.statusCode() == 200) {
            purchase.changeOrderStatus(OrderStatus.COMPLETED);
        } else {
            throw new CommonException(ResponseCode.ORDER_PAYMENT_NOT_APPROVED);
        }
    }
}
