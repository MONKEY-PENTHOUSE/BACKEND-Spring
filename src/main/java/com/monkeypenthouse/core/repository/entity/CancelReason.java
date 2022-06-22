package com.monkeypenthouse.core.repository.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CancelReason {
    CHANGE_OF_MIND("고객 변심"), EVENT_CANCELLED("이벤트 취소");

    private final String value;

    CancelReason(String value) { this.value = value; }

    @JsonCreator
    public static CancelReason from(String s) {
        return CancelReason.valueOf(s);
    }

    public String value() { return value; }
}