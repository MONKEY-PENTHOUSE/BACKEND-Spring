package com.monkeypenthouse.core.common;

public class ResponseMessage {
    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGIN_FAIL = "로그인 실패";
    public static final String UNAUTHORIZED_TOKEN = "토큰이 유효하지 않습니다.";
    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String NOT_FOUND_USER = "회원을 찾을 수 없습니다.";
    public static final String NO_MORE_ROOM = "빈 방이 부족합니다.";
    public static final String CREATED_USER = "회원 가입 성공";
    public static final String DUPLICATE_USER = "중복된 회원 정보";
    public static final String UPDATE_USER = "회원 정보 수정 성공";
    public static final String DELETE_USER = "회원 탈퇴 성공";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String DB_ERROR = "데이터베이스 에러";
    public static final String DUPLICATE_PHONE_NUM = "이미 존재하는 회원의 전화번호";
    public static final String SEND_SMS = "문자 전송 성공";
    public static final String SEND_SMS_FAIL = "문자 전송 실패";
    public static final String REISSUE_SUCCESS = "재발급 성공";
    public static final String REISSUE_FAIL = "재발급 실패";
    public static final String ADDITIONAL_INFO_REQUIRED = "추가 정보 입력 필요";
    public static final String INVALID_INPUTS = "유효하지 않은 요청 정보";
    public static final String JSON_PARSING_ERROR = "메시지 파싱 오류";
    public static final String LIFESTYLE_TEST_NEEDED = "라이프 스타일 테스트 필요";
}
