package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum LoginType {
    LOCAL, KAKAO, APPLE, NAVER;

    @JsonCreator
    public static LoginType from(String s) {
        return LoginType.valueOf(s.toUpperCase());
    }
}
