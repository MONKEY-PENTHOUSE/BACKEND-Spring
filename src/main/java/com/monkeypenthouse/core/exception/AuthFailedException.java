package com.monkeypenthouse.core.exception;

public class AuthFailedException extends ExpectedException {

    public AuthFailedException() {
        super(401, "회원 인증에 실패하였습니다.");
    }

    public AuthFailedException(String message) {
        super(401, message);
    }

    public AuthFailedException(int statusCode, String message) {
        super(statusCode, message);
    }
}
