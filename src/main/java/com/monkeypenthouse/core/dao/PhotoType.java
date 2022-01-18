package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PhotoType {
    BANNER, DETAIL;

    @JsonCreator
    public static PhotoType from(String s) {
        return PhotoType.valueOf(s.toUpperCase());
    }
}
