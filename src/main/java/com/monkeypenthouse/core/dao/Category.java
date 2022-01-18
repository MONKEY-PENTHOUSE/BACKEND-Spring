package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    PARTY_ROOM(1), FITNESS_CENTER(2), THEATER(3), CREATIVE_LAB(4);

    private final int value;

    private Category(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @JsonCreator
    public static Category from(String s) {
        return Category.valueOf(s.toUpperCase());
    }

    @JsonCreator
    public static Category from(int v) {
        switch (v) {
            case 1:
                return PARTY_ROOM;
            case 2:
                return FITNESS_CENTER;
            case 3:
                return THEATER;
            case 4:
                return CREATIVE_LAB;
            default:
                return null;
        }
    }
}