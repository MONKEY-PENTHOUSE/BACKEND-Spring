package com.monkeypenthouse.core.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetTicketOfOrderedResponseVo {
    private String amenityTitle;

    private List<TicketOfOrderedVo> tickets;

    private int totalPrice;
}
