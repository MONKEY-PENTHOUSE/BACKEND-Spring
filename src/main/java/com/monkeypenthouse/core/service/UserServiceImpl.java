package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.apache.catalina.security.SecurityUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    // 회원 추가
    @Override
    @Transactional
    public User add(User user) throws Exception {
        return userRepository.save(user);
    }

    // Id로 회원 조회
    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저 정보 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmailAndLoginType(String email, LoginType loginType) throws Exception {
        return userRepository.findByEmailAndLoginType(email, loginType)
                .orElseThrow(() -> new RuntimeException("유저 정보 없습니다."));
    }

    @Override
    public boolean checkIdDuplicate(String email) throws Exception {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkNameDuplicate(String name) throws Exception {
        return userRepository.existsByName(name);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public User getMyInfo() throws Exception {
//        return userRepository.findById(SecurityUtil.getCurrentUserId())
//                .orElseThrow(() -> new RuntimeException("유저 정보 없습니다."));
//    }
}
