package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.User;

public interface UserService {

    // 회원가입
    User add(User user) throws Exception;

    // id에 따른 조회
    User getById(Long id) throws Exception;

    // 이메일에 따른 조회
    User getUserByEmailAndLoginType(String email, LoginType loginType) throws Exception;

    // 특정 이메일의 회원이 존해자는지 확인
    boolean checkIdDuplicate(String email) throws Exception;

    // 특정 이메일의 회원이 존해자는지 확인
    boolean checkNameDuplicate(String name) throws Exception;

//    // 현재 SecurityContext에 있는 유저 정보 가져오기
//    User getMyInfo() throws Exception;
}
