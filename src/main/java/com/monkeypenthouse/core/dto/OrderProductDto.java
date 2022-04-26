package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.OrderProductVo;
import lombok.Builder;
import lombok.Getter;

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