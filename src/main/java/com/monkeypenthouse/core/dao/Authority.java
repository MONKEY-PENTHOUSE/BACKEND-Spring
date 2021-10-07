package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum Authority {
    USER, ADMIN;

    @JsonCreator
    public static Authority from(String s) {
        return Authority.valueOf(s.toUpperCase());
    }
}
