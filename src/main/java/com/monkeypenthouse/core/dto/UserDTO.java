package com.monkeypenthouse.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkeypenthouse.core.dao.Authority;
import com.monkeypenthouse.core.dao.LifeStyle;
import com.monkeypenthouse.core.dao.LoginType;
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
    public static class signupReqDTO {
        private String name;
        @JsonFormat(pattern = "yyyy.MM.dd")
        private LocalDate birth;
        // 0: 여성, 1: 남성
        private int gender;
        private String email;
        private String password;
        private String phoneNum;
        // 1: 동의 0: 비동의
        private int infoReceviable;
        private LifeStyle lifeStyle;
        private Authority authority;
        private LoginType loginType;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class MyUserResDTO {
        private Long id;
        private String name;
        @JsonFormat(pattern = "yyyy.MM.dd")
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

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LoginResDTO {
        private Long id;
        private String name;
        @JsonFormat(pattern = "yyyy.MM.dd")
        private LocalDate birth;
        // 0: 여성, 1: 남성
        private int gender;
        private String email;
        private String phoneNum;
        private String roomId;
        private String grantType;
        private String accessToken;
        private Long accessTokenExpiresIn;
        private String refreshToken;
        private Long refreshTokenExpiresIn;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FindEmailReqDTO {
        private String name;
        private String phoneNum;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FindEmailResDTO {
        private Long id;
        private String email;
        private LoginType loginType;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ChangePwReqDTO {
        private Long id;
        private String password;
    }
}
