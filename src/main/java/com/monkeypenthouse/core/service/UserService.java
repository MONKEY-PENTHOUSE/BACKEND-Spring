package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.Tokens;
import com.monkeypenthouse.core.dao.User;

import java.util.Optional;

public interface UserService {

    // 회원가입
    User add(User user) throws Exception;

    // id에 따른 조회
    User getById(Long id) throws Exception;

    // 이메일에 따른 조회
    User getUserByEmail(String email) throws Exception;

    // 이메일에 따른 조회
    User getUserByEmailAndLoginType(String email, LoginType loginType) throws Exception;

    // 특정 이메일의 회원이 존해자는지 확인
    boolean checkIdDuplicate(String email) throws Exception;

    // 특정 이메일의 회원이 존해자는지 확인
    boolean checkNameDuplicate(String name) throws Exception;

    // 로그인
    Tokens login(User user) throws Exception;

    // accessToken 재발급
    Tokens reissue(Tokens tokens);

    // 현재 SecurityContext에 있는 유저 정보 가져오기
    User getMyInfo() throws Exception;

    // 카카오톡 인증 후 회원 정보 조회
    // 유저 정보가 없을 시 비어있는 유저 리턴
    User authKakao(String code) throws Exception;

    User authNaver(String s, String code) throws Exception;

    Optional<User> findEmail(User user);

    int changePassword(User user) throws Exception ;
}
