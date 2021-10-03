package com.monkeypenthouse.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RoomDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SignUpRoomDTO {
        private String id;
    }
}
