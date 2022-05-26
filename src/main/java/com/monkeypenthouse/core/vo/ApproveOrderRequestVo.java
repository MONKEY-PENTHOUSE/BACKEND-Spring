package com.monkeypenthouse.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveOrderRequestVo {

    private String paymentKey;
    private String orderId;
    private Integer amount;
}