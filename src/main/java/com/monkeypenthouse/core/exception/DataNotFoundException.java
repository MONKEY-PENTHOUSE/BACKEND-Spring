package com.monkeypenthouse.core.exception;

public class DataNotFoundException extends ExpectedException {

    private final Object target;

    public DataNotFoundException(Object target) {
        super(401, "해당 정보(" + target.getClass().getName() + ")가 존재하지 않습니다.");
        this.target = target;
    }

    public Object getRequestedTarget() {
        return target;
    }
}
