package com.monkeypenthouse.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class TicketDTO {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class saveDTO {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Pattern(regexp = "^.{1,30}$")
        private String name;

        @NotBlank(message = "상세 설명은 필수 입력값입니다.")
        @Pattern(regexp = "^.{1,50}$")
        private String detail;

        @NotNull(message = "정원은 필수 입력값입니다.")
        @Min(value = 0)
        private int capacity;

        @NotNull(message = "가격은 필수 입력값입니다.")
        @Min(value = 0)
        private int price;

        @NotNull(message = "이벤트 날짜는 필수 입력값입니다.")
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        private LocalDateTime eventDateTime;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class orderDTO {
        @Positive
        @NotNull(message = "아이디는 필수 입력값입니다.")
        private long id;

        @Positive
        @NotNull(message = "수량은 필수 입력값입니다.")
        private int capacity;

    }
}
