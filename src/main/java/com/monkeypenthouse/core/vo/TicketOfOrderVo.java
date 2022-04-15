package com.monkeypenthouse.core.vo;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
public class TicketOfOrderVo {
    private Long id;
    private Long capacity;

    public Long getId() {
        return id;
    }
}
