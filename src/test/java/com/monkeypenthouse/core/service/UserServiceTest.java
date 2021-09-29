package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dao.Authority;
import com.monkeypenthouse.core.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Transactional
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유효한 일반 회원 정보를 주면 DB에 회원 추가한다.")
    public void saveUser() throws Exception {
        User user = User.builder()
                .name("테스트사용자3")
                .birth(LocalDate.of(1998, 1, 28))
                .gender(0)
                .email("user333@monkeypenthouse.com")
                .password(passwordEncoder.encode("1111"))
                .phoneNum("01012345888")
                .personalInfoCollectable(1)
                .infoReceivable(1)
                .loginType(LoginType.LOCAL)
                .authority(Authority.USER)
                .build();

        userService.add(user);
        Assertions.assertThat(
                userRepository
                        .findByEmailAndLoginType("user333@monkeypenthouse.com", LoginType.LOCAL)
                .get().getName()).isEqualTo("테스트사용자3");
    }

}
