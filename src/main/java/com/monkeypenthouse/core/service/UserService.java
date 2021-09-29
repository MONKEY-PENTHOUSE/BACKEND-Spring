package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dao.UserDTO;
import com.monkeypenthouse.core.dao.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    // 회원가입
    User add(User user);

    // id에 따른 조회
    User getById(Long id);
}
