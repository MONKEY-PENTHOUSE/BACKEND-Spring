package com.monkeypenthouse.core.controller.dto.amenity;

import com.monkeypenthouse.core.controller.dto.ticket.TicketOfOrderedI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AmenityTicketsOfOrderedResI {
    private final String amenityTitle;
    private final List<TicketOfOrderedI> tickets;
    private final int totalPrice;
}
