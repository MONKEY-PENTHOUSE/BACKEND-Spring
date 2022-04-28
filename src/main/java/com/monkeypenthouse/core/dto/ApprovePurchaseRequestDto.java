package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.ApproveOrderRequestVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApprovePurchaseRequestDto {

    private String paymentKey;
    private String orderId;
    private Integer amount;

    public ApproveOrderRequestVo toVo() {
        return ApproveOrderRequestVo.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}