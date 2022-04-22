package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CreateOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderRequestVo.OrderProductVo;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CreateOrderRequestDto {

    private final List<OrderProductDto> orderProductDtoList;

    @Getter
    @Builder
    public class OrderProductDto {

        public Long ticketId;
        public Integer quantity;

        OrderProductVo toVo() {
            return OrderProductVo.builder()
                    .ticketId(ticketId)
                    .quantity(quantity)
                    .build();
        }
    }

    public CreateOrderRequestVo toVo() {
        return CreateOrderRequestVo.builder()
                .orderProductVoList(orderProductDtoList.stream().map(o -> o.toVo()).collect(Collectors.toList()))
                .build();
    }
}
