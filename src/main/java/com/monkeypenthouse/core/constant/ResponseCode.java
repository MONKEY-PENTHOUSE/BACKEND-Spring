package com.monkeypenthouse.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS("Success", HttpStatus.OK),

    USER_NOT_FOUND("회원이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    DATA_NOT_FOUND("데이터가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    DATA_DUPLICATED("데이터가 중복되었습니다.", HttpStatus.CONFLICT),

    KAKAO_LOGIN_FAILED("카카오 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
    NAVER_LOGIN_FAILED("네이버 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
    NOT_AUTHENTICATED_USER("로그인된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED),
    TOKENS_NOT_MATCHED("토큰이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    PHONE_NUMBER_DUPLICATED("이미 가입된 회원의 전화번호입니다.", HttpStatus.FORBIDDEN),
    PASSWORD_NOT_MATCHED("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    EMPTY_ROOM_NOT_EXISTED("빈 방이 없습니다.", HttpStatus.BAD_REQUEST),

    LIFE_STYLE_TEST_NEEDED("라이프스타일 테스트 미완료 회원입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
}
