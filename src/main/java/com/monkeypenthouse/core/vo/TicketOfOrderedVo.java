package com.monkeypenthouse.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketOfOrderedVo {

    private Long id;

    private LocalDateTime eventDateTime;

    private String name;

    private String detail;

    private int price;

    private int amount;
}
