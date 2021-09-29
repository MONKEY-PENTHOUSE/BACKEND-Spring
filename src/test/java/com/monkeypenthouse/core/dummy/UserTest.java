package com.monkeypenthouse.core.dummy;

import com.monkeypenthouse.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Test
//    @DisplayName("user 더미데이터 넣는 테스트")
//   public void insertDummies() {
//        // 1-80 : USER
//        // 81-100 : USER, ADMIN
//        IntStream.rangeClosed(1, 100).forEach(i -> {
//            User user = User.builder()
//                    .name("사용자"+i)
//                    .birth(LocalDateTime.of(1997, i % 12 + 1, 28, 14, 25, 00))
//                    .gender(0)
//                    .email("user" + i + "@monkeypenthouse.com")
//                    .password(passwordEncoder.encode("1111"))
//                    .phoneNum("01012345"+i)
//                    .personalInfoCollectable(1)
//                    .infoReceivable(1)
//                    .roomNum("A" + i)
//                    .loginType(LoginType.LOCAL)
//                    .build();
//            user.addUserRole(UserRole.USER);
//
//            if (i > 80) {
//                user.addUserRole(UserRole.ADMIN);
//            }
//
//            repository.save(user);
//        });
//    }

//    @Test
//    @DisplayName("이메일과 로그인 타입으로 회원을 조회할 수 있다.")
//    public void findUserByEmailAndLoginType() {
//        Optional<User> result = repository.findByEmailAndLoginType("user1@monkeypenthouse.com", LoginType.LOCAL);
//        User user = result.get();
//        System.out.println("user = " + user);
//    }
}
