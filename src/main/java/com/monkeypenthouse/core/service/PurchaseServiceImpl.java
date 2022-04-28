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
import com.monkeypenthouse.core.vo.CreateOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import lombok.RequiredArgsConstructor;
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

    @Value("${toss-payments.api-key}")
    private String tossPaymentsApiKey;

    @Override
    @Transactional
    public CreateOrderResponseVo createPurchase(final UserDetails userDetails, final CreateOrderRequestVo requestVo) {

        // 주문 수량에 대해 티켓 재고를 확인하는 유효성 검증 로직 (레디스를 사용할 예정)

        // 티켓 리스트 불러오기
        final List<Long> ticketIdList = requestVo.getOrderProductVoList().stream()
                .map(orderProductVo -> orderProductVo.getTicketId())
                .collect(Collectors.toList());

        final List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAllById(ticketIdList);

        // 티켓 리스트 유효성 검증
        if (ticketList.size() != requestVo.getOrderProductVoList().size()) {
            throw new CommonException(ResponseCode.TICKET_NOT_FOUND);
        }

        // 티켓 ID : 구매 개수 HashMap 구성
        final HashMap<Long, Integer> quantityMap = new HashMap<>();
        requestVo.getOrderProductVoList().stream().map(
                orderProductVo -> quantityMap.put(orderProductVo.getTicketId(), orderProductVo.getQuantity()));

        // amount 측정
        int amount = ticketList.stream()
                .mapToInt(t -> quantityMap.get(t.getId()) * t.getPrice())
                .sum();

        // orderId 생성
        final String orderId = orderIdGenerator.generate();

        // orderName 생성
        String orderName = ticketList.get(0).getName();
        if (ticketList.size() > 1) {
            orderName += " 외 " + (ticketList.size() - 1) + "건";
        }

        // Order 엔티티 생성
        // 여기서 User 엔티티를 find하지 않고 바로 foreign key로 주입할 수 있는 방법이 있나요..?
        final User user = userService.getUserByEmail(userDetails.getUsername());

        final Purchase purchase = new Purchase(user, orderId, orderName, amount, OrderStatus.IN_PROGRESS);
        purchaseRepository.save(purchase);

        // OrderProduct 엔티티 생성
        ticketList.stream().map(ticket ->
                purchaseTicketMappingRepository.save(new PurchaseTicketMapping(purchase, ticket, quantityMap.get(ticket.getId())))
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
        }
        else {
            throw new CommonException(ResponseCode.ORDER_PAYMENT_NOT_APPROVED);
        }
    }
}
