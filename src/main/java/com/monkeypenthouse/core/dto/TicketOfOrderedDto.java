package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.TicketOfAmenityVo;
import com.monkeypenthouse.core.vo.TicketOfOrderedVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketOfOrderedDto {

    private Long id;

    private LocalDateTime eventDateTime;

    private String name;

    private String detail;

    private int price;

    private int amount;

    public static TicketOfOrderedDto of(TicketOfOrderedVo vo) {
        return builder()
                .id(vo.getId())
                .eventDateTime(vo.getEventDateTime())
                .name(vo.getName())
                .detail(vo.getDetail())
                .price(vo.getPrice())
                .amount(vo.getAmount())
                .build();
    }
}
