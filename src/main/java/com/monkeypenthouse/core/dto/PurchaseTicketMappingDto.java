package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.PurchaseTicketMappingVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseTicketMappingDto {

    public Long amenityId;
    public Long ticketId;
    public Integer quantity;

    PurchaseTicketMappingVo toVo() {
        return PurchaseTicketMappingVo.builder()
                .amenityId(amenityId)
                .ticketId(ticketId)
                .quantity(quantity)
                .build();
    }
}