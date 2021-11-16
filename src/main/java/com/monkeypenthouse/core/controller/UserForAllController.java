package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.dao.*;
import com.monkeypenthouse.core.dto.RoomDTO.*;
import com.monkeypenthouse.core.dto.TokenDTO.*;
import com.monkeypenthouse.core.dto.UserDTO.*;
import com.monkeypenthouse.core.service.MessageService;
import com.monkeypenthouse.core.service.RoomService;
import com.monkeypenthouse.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/all/")
@Log4j2
@RequiredArgsConstructor
public class UserForAllController {

    private final UserService userService;
    private final RoomService roomService;
    private final MessageService messageService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultRes<?>> signUp(@RequestBody signupReqDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);
            // 회원 추가
            user = userService.add(user);

            // 회원에게 빈방 주기
            Room room = roomService.giveVoidRoomForUser(user);
            SignUpRoomDTO roomDTO = SignUpRoomDTO.builder()
                    .id(room.getId())
                    .build();
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.CREATED.value(), ResponseMessage.CREATED_USER, roomDTO),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "/check-id-duplication")
    public ResponseEntity<DefaultRes<?>> checkIdDuplicate(@RequestParam("email") String email) {
        try {
            boolean exist = userService.checkIdDuplicate(email);
            if (exist) {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER, true),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER, false),
                        HttpStatus.OK
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "/check-name-duplication")
    public ResponseEntity<DefaultRes<?>> checkNameDuplicate(@RequestParam("name") String name) {
        try {
            boolean exist = userService.checkNameDuplicate(name);
            if (exist) {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER, true),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER, false),
                        HttpStatus.OK
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping(value = "/sms-auth")
    public ResponseEntity<DefaultRes<?>> smsAuth(@RequestBody Map<String, String> map) {
        try {
            String phoneNum = map.get("phoneNum");
            messageService.sendSMS(phoneNum);

            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.SEND_SMS),
                    HttpStatus.OK
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.SEND_SMS_FAIL),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "/check-sms-auth")
    public ResponseEntity<DefaultRes<?>> checkSmsAuth(@RequestBody Map<String, String> map) {
        try {
            String phoneNum = map.get("phoneNum");
            String authNum = map.get("authNum");
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER,
                            messageService.checkAuthNum(phoneNum, authNum)),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<DefaultRes<?>> loginLocal(@RequestBody LoginReqDTO userDTO, HttpServletResponse response) {
            User user = modelMapper.map(userDTO, User.class);
            Tokens tokens = userService.login(user);
            Cookie cookie = new Cookie("refreshToken", tokens.getRefreshToken());
            cookie.setMaxAge(60 * 60 * 24 * 14);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

        try {
            User optionalUser = userService.getUserByEmail(userDTO.getEmail());
            LoginResDTO loginResDTO = modelMapper.map(optionalUser, LoginResDTO.class);
            loginResDTO.setGrantType(tokens.getGrantType());
            loginResDTO.setAccessToken(tokens.getAccessToken());
            loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());

            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS,
                            loginResDTO
                    ), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("e = " + e);
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.LOGIN_FAIL),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

    @GetMapping(value = "/login/kakao")
    @ResponseBody
    public ResponseEntity<DefaultRes<?>> authKakao(@RequestParam("token") String token, HttpServletResponse response) {
        try {
            User user;
            try {
                user = userService.authKakao(token);
            } catch (Exception e) {
                if (e.getMessage().equals("유효하지 않은 토큰")) {
                    return new ResponseEntity<>(
                            DefaultRes.res(HttpStatus.UNAUTHORIZED.value(), ResponseMessage.UNAUTHORIZED_TOKEN),
                            HttpStatus.UNAUTHORIZED
                    );
                } else {
                    throw e;
                }
            }

            // 유저 정보가 있으면 로그인 처리
            if (user.getId() != null) {
                Tokens tokens = userService.login(user);
                LoginResDTO loginResDTO = modelMapper.map(user, LoginResDTO.class);
                Cookie cookie = new Cookie("refreshToken", tokens.getRefreshToken());
                cookie.setMaxAge(60 * 60 * 24 * 14);
                // cookie.setSecure(true);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);

                loginResDTO.setGrantType(tokens.getGrantType());
                loginResDTO.setAccessToken(tokens.getAccessToken());
                loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());

                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS,
                                loginResDTO),
                        HttpStatus.OK
                );
            }
            // 유저 정보가 없으면 회원가입을 위해 기본 정보 보내주기
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.FORBIDDEN.value(), ResponseMessage.SIGN_UP_REQUIRED,
                            modelMapper.map(user, signupReqDTO.class)),
                    HttpStatus.FORBIDDEN
            );
        } catch (Exception e) {
            System.out.println("e = " + e.getMessage());
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "/login/naver")
    @ResponseBody
    public ResponseEntity<DefaultRes<?>> authNaver(@RequestParam("code") String code,
                                                   @RequestParam("state") String state,
                                                   @RequestParam("error") @Nullable String error,
                                                   @RequestParam("error_description") @Nullable String errorDescription,
                                                   HttpServletResponse response) {
        try {
            User user = userService.authNaver(code, state);
            // 유저 정보가 있으면 로그인 처리
            if (user.getId() != null) {
                Tokens tokens = userService.login(user);
                Cookie cookie = new Cookie("refreshToken", tokens.getRefreshToken());
                cookie.setMaxAge(60 * 60 * 24 * 14);
                // cookie.setSecure(true);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS,
                                modelMapper.map(userService.login(user), LoginResDTO.class)),
                        HttpStatus.OK
                );
            }
            // 유저 정보가 없으면 회원가입을 위해 기본 정보 보내주기
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS,
                            modelMapper.map(user, signupReqDTO.class)),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            System.out.println("e = " + e.getMessage());
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<DefaultRes<?>> reissue(@CookieValue("refreshToken") String refreshToken, @RequestBody ReissueReqDTO tokenDTO) {
        try {
            Tokens tokens = modelMapper.map(tokenDTO, Tokens.class);
            tokens.setRefreshToken(refreshToken);

            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.REISSUE_SUCCESS,
                            modelMapper.map(userService.reissue(tokens), ReissueResDTO.class)),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.REISSUE_FAIL),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "/find-email")
    public ResponseEntity<DefaultRes<?>> findEmail(@RequestBody FindEmailReqDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);
            Optional<User> optionalUser = userService.findEmail(user);

            if (optionalUser.isPresent()) {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER,
                                modelMapper.map(optionalUser.get(), FindEmailResDTO.class)),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.UNAUTHORIZED.value(), ResponseMessage.NOT_FOUND_USER),
                        HttpStatus.UNAUTHORIZED
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping(value = "/change-pw")
    public ResponseEntity<DefaultRes<?>> changePassword(@RequestBody ChangePwReqDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);
            int result = userService.changePassword(user);

            if (result == 1) {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.UPDATE_USER),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.UNAUTHORIZED.value(), ResponseMessage.NOT_FOUND_USER),
                        HttpStatus.UNAUTHORIZED
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
