package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CompleteOrderRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrderRequestDto {

    private String paymentKey;
    private String orderId;
    private Integer amount;

    public CompleteOrderRequestVo toVo() {
        return CompleteOrderRequestVo.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
