package com.monkeypenthouse.core.repository.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CancelReason {
    CHANGE_OF_MIND("고객 변심"), MINIMUM_NUMBER_NOT_MET("최소 인원 미달성");

    private final String value;

    CancelReason(String value) { this.value = value; }

    @JsonCreator
    public static CancelReason from(String s) {
        return CancelReason.valueOf(s);
    }

    public String value() { return value; }
}