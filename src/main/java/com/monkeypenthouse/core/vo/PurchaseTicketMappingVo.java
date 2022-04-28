package com.monkeypenthouse.core.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseTicketMappingVo {

    public Long amenityId;
    public Long ticketId;
    public Integer quantity;
}