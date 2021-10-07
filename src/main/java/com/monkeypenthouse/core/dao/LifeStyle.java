package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LifeStyle {
    OPTIMIST, ADVENTURER, INVENTOR, LEADER, DREAMER, GUARDIAN, SOCIABLE, ARTIST;

    @JsonCreator
    public static LifeStyle from(String s) {
        return LifeStyle.valueOf(s.toUpperCase());
    }
}
