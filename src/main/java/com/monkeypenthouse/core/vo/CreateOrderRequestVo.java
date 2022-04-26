package com.monkeypenthouse.core.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateOrderRequestVo {

    private final List<OrderProductVo> orderProductVoList;


}
