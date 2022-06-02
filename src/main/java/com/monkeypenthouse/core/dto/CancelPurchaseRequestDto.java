package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CancelPurchaseRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelPurchaseRequestDto {

    private String orderId;

    public CancelPurchaseRequestVo toVo() {
        return CancelPurchaseRequestVo.builder()
                .orderId(orderId)
                .build();
    }
}