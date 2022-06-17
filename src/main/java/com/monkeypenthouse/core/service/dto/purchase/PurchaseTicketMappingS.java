package com.monkeypenthouse.core.service.dto.purchase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PurchaseTicketMappingS {
    private final Long amenityId;
    private final Long ticketId;
    private final Integer quantity;
}
