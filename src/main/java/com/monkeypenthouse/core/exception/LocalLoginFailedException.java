package com.monkeypenthouse.core.exception;

public class LocalLoginFailedException extends AuthFailedException {

    public LocalLoginFailedException() {
        super("몽키 펜트하우스 로그인에 실패하였습니다.");
    }

    public LocalLoginFailedException(boolean emailNotFound) {
        super(emailNotFound ? "해당 이메일을 가진 회원이 없습니다." : "비밀번호가 일치하지 않습니다.");
    }
}
