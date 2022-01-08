package com.monkeypenthouse.core.exception;

public class ExpectedException extends Exception {

    private final int statusCode;

    public ExpectedException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }


}
