package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CreateOrderRequestVo;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CreateOrderRequestDto {

    private final List<OrderProductDto> orderProductDtoList;

    public CreateOrderRequestVo toVo() {
        return CreateOrderRequestVo.builder()
                .orderProductVoList(orderProductDtoList.stream().map(o -> o.toVo()).collect(Collectors.toList()))
                .build();
    }
}
