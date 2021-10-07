package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.dao.LifeStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

public class UserDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LocalSignUpResDTO {
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
        private LifeStyle lifeStyle;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class MyUserResDTO {
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

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LoginReqDTO {
        private String email;
        private String password;
    }
}
