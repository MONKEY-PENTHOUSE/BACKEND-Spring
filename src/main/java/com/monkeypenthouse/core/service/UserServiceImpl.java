package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dao.UserDTO;
import com.monkeypenthouse.core.dao.UserRole;
import com.monkeypenthouse.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    // 회원 추가
    @Override
    public User add(User user) {
        return userRepository.save(user);
    }

    // Id로 회원 조회
    @Override
    public User getById(Long id) {
        return userRepository.findById(id).get();
    }
}
