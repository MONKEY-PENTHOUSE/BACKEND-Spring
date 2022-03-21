package com.monkeypenthouse.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkeypenthouse.core.dto.toss.ApprovePaymentResponseDto;
import com.monkeypenthouse.core.entity.Order;
import com.monkeypenthouse.core.entity.OrderStatus;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.exception.PaymentFailedException;
import com.monkeypenthouse.core.repository.OrderRepository;
import com.monkeypenthouse.core.vo.CompleteOrderRequestVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Value("${toss-payments.api-key}")
    private String tossPaymentsApiKey;

    @Transactional
    public void completeOrder(final CompleteOrderRequestVo requestVo) throws DataNotFoundException, IOException, InterruptedException {

        final Order order = orderRepository.findByOrderId(requestVo.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(Order.builder().orderId(requestVo.getOrderId()).build()));

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
            order.setOrderStatus(OrderStatus.COMPLETED);
        }
        else {
            throw new PaymentFailedException();
        }
    }
}
