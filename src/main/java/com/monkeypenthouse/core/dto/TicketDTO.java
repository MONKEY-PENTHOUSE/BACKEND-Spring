package com.monkeypenthouse.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class TicketDTO {
    @Getter
    @Setter
    public static class SaveDTO {
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
        @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm")
        private LocalDateTime eventDateTime;
    }
}
