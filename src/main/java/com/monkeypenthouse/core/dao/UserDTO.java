package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LocalSignUpDTO {
        private String name;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birth;
        // 0: 여성, 1: 남성
        private int gender;
        private String email;
        private String password;
        private String phoneNum;
        // 1: 동의 0: 비동의
        private int personalInfoCollectable;
        // 1: 동의 0: 비동의
        private int infoReceviable;
    }

    public static class MyUserDTO {
        private Long id;
        private String name;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birth;
        // 0: 여성, 1: 남성
        private int gender;
        private String email;
        private String phoneNum;
        private String roomId;
    }
}
