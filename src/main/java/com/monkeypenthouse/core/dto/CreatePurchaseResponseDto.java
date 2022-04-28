package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePurchaseResponseDto {

    public Integer amount;
    public String orderId;
    public String orderName;

    public static CreatePurchaseResponseDto of(CreateOrderResponseVo vo) {
        return builder()
                .amount(vo.getAmount())
                .orderId(vo.getOrderId())
                .orderName(vo.getOrderName())
                .build();
    }
}
