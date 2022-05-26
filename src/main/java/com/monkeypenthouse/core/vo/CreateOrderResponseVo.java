package com.monkeypenthouse.core.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrderResponseVo {

    public Integer amount;
    public String orderId;
    public String orderName;
}
