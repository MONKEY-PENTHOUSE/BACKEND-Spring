package com.monkeypenthouse.core.dto;

import lombok.*;

public class TicketDTO {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class saveDTO {
        private String name;
        private String detail;
        private int capacity;
        private int price;
    }
}
