package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum Authority {
    USER, ADMIN;

    @JsonCreator
    public static Authority from(String s) {
        return Authority.valueOf(s.toUpperCase());
    }
}
