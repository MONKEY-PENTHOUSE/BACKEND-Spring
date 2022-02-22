package com.monkeypenthouse.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

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
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        private LocalDateTime eventDateTime;
    }
}
