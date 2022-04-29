package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CancelPurchaseRequestVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CancelPurchaseRequestDto {

    private String orderId;

    public CancelPurchaseRequestVo toVo() {
        return CancelPurchaseRequestVo.builder()
                .orderId(orderId)
                .build();
    }
}