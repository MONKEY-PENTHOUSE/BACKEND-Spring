package com.monkeypenthouse.core.exception;

import org.springframework.http.HttpStatus;

public class ExpectedException extends Exception {

    private final HttpStatus statusCode;

    public ExpectedException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
