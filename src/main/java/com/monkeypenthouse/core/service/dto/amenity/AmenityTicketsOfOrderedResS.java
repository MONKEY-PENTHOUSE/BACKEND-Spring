package com.monkeypenthouse.core.service.dto.amenity;

import com.monkeypenthouse.core.controller.dto.amenity.AmenityTicketsOfOrderedResI;
import com.monkeypenthouse.core.service.dto.ticket.TicketOfOrderedS;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Builder
public class AmenityTicketsOfOrderedResS {
    private final String amenityTitle;
    private final List<TicketOfOrderedS> tickets;
    private final int totalPrice;

    public AmenityTicketsOfOrderedResI toI() {
        return new AmenityTicketsOfOrderedResI(
                amenityTitle, tickets.stream().map(TicketOfOrderedS::toI).collect(Collectors.toList()), totalPrice
        );
    }
}
