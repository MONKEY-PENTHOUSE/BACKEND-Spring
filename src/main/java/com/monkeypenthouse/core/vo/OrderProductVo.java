package com.monkeypenthouse.core.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProductVo {

    public Long ticketId;
    public Integer quantity;
}