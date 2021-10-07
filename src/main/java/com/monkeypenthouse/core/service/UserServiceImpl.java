package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.RefreshToken;
import com.monkeypenthouse.core.dao.Tokens;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.repository.RefreshTokenRepository;
import com.monkeypenthouse.core.repository.UserRepository;
import com.monkeypenthouse.core.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

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

    @Override
    @Transactional
    public Tokens login(User user) {
        // 1. Login ID/PW를 기반으로 authenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        // 2. 실제로 검증이 이뤄지는 부분
        // authentication 메서드가 실행이 될 때 CustomUserDetailService에서 만들었던 loadUserByUser
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰
        Tokens tokens = tokenProvider.generateTokens(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokens.getRefreshToken())
                .build();

        return tokens;
    }

//   @Override
//    @Transactional(readOnly = true)
//    public User getMyInfo() throws Exception {
//        return userRepository.findById(SecurityUtil.getCurrentUserId())
//                .orElseThrow(() -> new RuntimeException("유저 정보 없습니다."));
//    }
}
