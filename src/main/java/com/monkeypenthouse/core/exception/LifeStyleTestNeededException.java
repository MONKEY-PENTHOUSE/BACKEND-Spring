package com.monkeypenthouse.core.exception;

import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dto.UserDTO.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class LifeStyleTestNeededException extends AuthFailedException {

    private final User user;
    @Autowired
    private ModelMapper modelMapper;

    public LifeStyleTestNeededException(User user) {
        super(403, "라이프스타일 테스트 미완료 회원입니다.");
        this.user = user;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<SignupResDTO> getUserDTO() {
        if (user != null) {
            return Optional.of(modelMapper.map(user, SignupResDTO.class));
        } else{
            return Optional.empty();
        }
    }

}
