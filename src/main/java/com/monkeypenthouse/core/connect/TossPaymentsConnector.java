package com.monkeypenthouse.core.connect;

import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.controller.dto.purchase.PurchaseApproveTossPayResI;
import com.monkeypenthouse.core.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class TossPaymentsConnector {
    @Value("${toss-payments.api-key}")
    private String tossPaymentsApiKey;
    private final ObjectMapper objectMapper;

    public PurchaseApproveTossPayResI approvePayments(String paymentKey, int amount, String orderId)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey))
                .header("Authorization", tossPaymentsApiKey)
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(
                        "{\"amount\":" + amount +
                                ",\"orderId\":\"" + orderId + "\"}"))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new CommonException(ResponseCode.ORDER_PAYMENT_NOT_APPROVED);
        }

        return objectMapper.readValue(response.body(), PurchaseApproveTossPayResI.class);
    }
}
