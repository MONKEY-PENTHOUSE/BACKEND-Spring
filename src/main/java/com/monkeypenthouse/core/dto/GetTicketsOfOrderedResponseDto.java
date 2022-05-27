package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.GetTicketOfOrderedResponseVo;
import com.monkeypenthouse.core.vo.GetViewedResponseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTicketsOfOrderedResponseDto {

    private String amenityTitle;

    private List<TicketOfOrderedDto> tickets;

    private int totalPrice;

    public static GetTicketsOfOrderedResponseDto of(GetTicketOfOrderedResponseVo vo) {
        return builder()
                .amenityTitle(vo.getAmenityTitle())
                .tickets(
                        vo.getTickets()
                                .stream()
                                .map(TicketOfOrderedDto::of)
                                .collect(Collectors.toList()))
                .totalPrice(vo.getTotalPrice())
                .build();
    }
}
