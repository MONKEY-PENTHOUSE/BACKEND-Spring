package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.connect.KakaoConnector;
import com.monkeypenthouse.core.connect.NaverConnector;
import com.monkeypenthouse.core.dao.*;
import com.monkeypenthouse.core.dto.KakaoUserDTO;
import com.monkeypenthouse.core.dto.NaverUserDTO;
import com.monkeypenthouse.core.repository.RefreshTokenRepository;
import com.monkeypenthouse.core.repository.RoomRepository;
import com.monkeypenthouse.core.repository.UserRepository;
import com.monkeypenthouse.core.security.PrincipalDetails;
import com.monkeypenthouse.core.security.SecurityUtil;
import com.monkeypenthouse.core.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoConnector kakaoConnector;
    private final NaverConnector naverConnector;

    // 회원 추가
    @Override
    @Transactional
    public User add(User user) throws Exception {
        try {
            // 회원 정보 저장
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setAuthority(Authority.USER);
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("회원 정보 중복");
        }

        // 회원에게 빈 방 주기
        roomRepository.updateUserIdForVoidRoom(user.getId(), user.getAuthority());
        Optional<Room> roomOptional = roomRepository.findByUserId(user.getId());
        Room room = roomOptional.orElseThrow(() -> new Exception("빈 방 없음"));
        userRepository.updateRoomId(user.getId(), room);

        user.setRoom(room);
        return user;
    }

    // Id로 회원 조회
    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("유저 정보 없습니다."));
    }

    @Override
    public User getUserByEmail(String email) throws Exception {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("유저 정보 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmailAndLoginType(String email, LoginType loginType) throws Exception {
        return userRepository.findByEmailAndLoginType(email, loginType)
                .orElseThrow(() -> new RuntimeException("유저 정보 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailDuplicate(String email) throws Exception {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkPhoneNumDuplicate(String phoneNum) throws Exception {
        return userRepository.existsByPhoneNum(phoneNum);
    }


    @Override
    @Transactional
    public Map<String, Object> login(User user) throws Exception {
        // 1. Login ID/PW를 기반으로 authenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        // 2. 실제로 검증이 이뤄지는 부분
        // authentication 메서드가 실행이 될 때 CustomUserDetailService에서 만들었던 loadUserByUser
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보에서 유저 정보 가져오기
        User loggedInUser = ((PrincipalDetails) authentication.getPrincipal()).getUserInfo();

        Map<String, Object> map = new HashMap<>();

        // 라이프스타일 테스트 미완료 회원 처리
        if (loggedInUser.getLifeStyle() == null) {
            map.put("user", loggedInUser);
        } else {
            // 4. 인증 정보를 토대로 JWT 토큰, RefreshToken 저장
            Tokens tokens = tokenProvider.generateTokens(authentication);
            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokens.getRefreshToken())
                    .build();
            refreshTokenRepository.save(refreshToken);

            map.put("user", loggedInUser);
            map.put("tokens", tokens);
        }
        return map;
    }

    @Override
    @Transactional
    public Tokens reissue(String refreshToken) throws Exception {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 3. 저장소에서 UserID를 기반으로 RefreshToken 값 가져옴
        RefreshToken savedRefreshToken = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자입니다."));

        // 4. refreshToken 일치하는지 검사
        if (!savedRefreshToken.getValue().equals(refreshToken.substring(7))) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        Tokens newTokens = tokenProvider.generateTokens(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = savedRefreshToken.updateValue(newTokens.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return newTokens;
    }

    @Override
    @Transactional(readOnly = true)
    public User getMyInfo() throws Exception {
        return userRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("유저 정보 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public User authKakao(String token) throws Exception {
        KakaoUserDTO kakaoUser;
        try {
            kakaoUser = kakaoConnector.getUserInfo(token);
        } catch (Exception e) {
            throw new Exception("유효하지 않은 토큰");
        }
        try {
            Optional<User> optionalUser = userRepository.findByEmailAndLoginType(kakaoUser.getKakao_account().getEmail(), LoginType.KAKAO);

            KakaoUserDTO finalKakaoUser = kakaoUser;

            return optionalUser.orElseGet(() -> User.builder()
                    .name(finalKakaoUser.getKakao_account().getProfile().getNickname())
                    .gender(finalKakaoUser.getKakao_account().isHas_gender() ? finalKakaoUser.getKakao_account().getGender().equals("female") ? 0 : 1 : 2)
                    .email(finalKakaoUser.getKakao_account().isHas_email() ? finalKakaoUser.getKakao_account().getEmail() : null)
                    .password(UUID.randomUUID().toString())
                    .loginType(LoginType.KAKAO)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("회원 정보를 찾는데 실패했습니다. : " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User authNaver(String token) throws Exception {
        NaverUserDTO naverUser;

        try {
            naverUser = naverConnector.getUserInfo(token);
        } catch (Exception e) {
            throw new Exception("유효하지 않은 토큰");
        }
        try {
            Optional<User> optionalUser = userRepository.findByEmailAndLoginType(naverUser.getResponse().getEmail(), LoginType.NAVER);

            NaverUserDTO finalNaverUser = naverUser;
            return optionalUser.orElseGet(() -> User.builder()
                    .name(finalNaverUser.getResponse().getName())
                    .gender(finalNaverUser.getResponse().getGender() == null ?
                            2 : finalNaverUser.getResponse().getGender().equals("F") ? 0 : 1)
                    .email(finalNaverUser.getResponse().getEmail())
                    .password(UUID.randomUUID().toString())
                    .phoneNum(finalNaverUser.getResponse().getMobile() == null ?
                            null : finalNaverUser.getResponse().getMobile().replace("-", ""))
                    .loginType(LoginType.NAVER)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("회원 정보를 찾는데 실패했습니다. : " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findEmail(User user) {
        return userRepository.findByNameAndPhoneNum(user.getName(), user.getPhoneNum());
    }

    @Override
    @Transactional
    public int updatePassword(User user) throws Exception {
        return userRepository.updatePassword(
                passwordEncoder.encode(user.getPassword()),
                user.getId());
    }

    @Override
    @Transactional
    public int updateLifeStyle(User user) throws Exception {
        return userRepository.updateLifeStyle(
                user.getLifeStyle(),
                user.getId());
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) throws Exception {
        Long id = userRepository.findByEmail(email).orElseThrow().getId();
        System.out.println("id = " + id);
        roomRepository.deleteUserId(id);
        userRepository.deleteById(id);
    }
}
