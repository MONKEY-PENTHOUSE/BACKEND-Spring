package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.common.SocialLoginRes;
import com.monkeypenthouse.core.dao.*;
import com.monkeypenthouse.core.dto.TokenDTO.*;
import com.monkeypenthouse.core.dto.UserDTO;
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

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.NoSuchElementException;
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
            try {
                user = userService.add(user);
            } catch (Exception e) {
                switch (e.getMessage()) {
                    case "회원 정보 중복":
                        return new ResponseEntity<>(
                                DefaultRes.res(HttpStatus.CONFLICT.value(), ResponseMessage.DUPLICATE_USER),
                                HttpStatus.CONFLICT
                        );
                    case "빈 방 없음":
                        return new ResponseEntity<>(
                                DefaultRes.res(HttpStatus.FORBIDDEN.value(), ResponseMessage.NO_MORE_ROOM),
                                HttpStatus.FORBIDDEN
                        );
                    default:
                        throw e;
                }
            }
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.CREATED.value(), ResponseMessage.CREATED_USER,
                            modelMapper.map(user, SignupResDTO.class)),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /* 회워가입 테스트 용 */
    @DeleteMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultRes<?>> delete(@RequestBody Map<String, String> map) {
        try {
            String email = map.get("email");
            try {
                // 회원 삭제
                userService.deleteByEmail(email);
            } catch (NoSuchElementException e) {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.NOT_FOUND_USER),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.DELETE_USER),
                    HttpStatus.OK
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
            boolean exist = userService.checkEmailDuplicate(email);
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

//    @GetMapping(value = "/check-name-duplication")
//    public ResponseEntity<DefaultRes<?>> checkNameDuplicate(@RequestParam("name") String name) {
//        try {
//            boolean exist = userService.checkNameDuplicate(name);
//            if (exist) {
//                return new ResponseEntity<>(
//                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER, true),
//                        HttpStatus.OK
//                );
//            } else {
//                return new ResponseEntity<>(
//                        DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.READ_USER, false),
//                        HttpStatus.OK
//                );
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(
//                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }

    @PostMapping(value = "/sms-auth")
    public ResponseEntity<DefaultRes<?>> smsAuth(@RequestBody Map<String, String> map) {
        try {

            String phoneNum = map.get("phoneNum");

            if (userService.checkPhoneNumDuplicate(phoneNum)) {
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.FORBIDDEN.value(), ResponseMessage.DUPLICATE_PHONE_NUM),
                        HttpStatus.FORBIDDEN
                );
            }

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

    @PostMapping(value = "/check-sms-auth")
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

    @PatchMapping(value = "/life-style")
    public ResponseEntity<DefaultRes<?>> updateLifeStyle(@RequestBody UpdateLSReqDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);
            int result = userService.updateLifeStyle(user);

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

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<DefaultRes<?>> loginLocal(@RequestBody LoginReqDTO userDTO, HttpServletResponse response) {
        User user = modelMapper.map(userDTO, User.class);
        Tokens tokens;
        try {
            Map<String, Object> map = userService.login(user);
            // 유저 정보 DTO에 담기
            User loggedInUser = (User) map.get("user");
            if ((tokens = (Tokens) map.get("tokens")) == null) {
                // 토큰 정보가 없으면 라이프스타일 미완료 유저 -> Forbidden 처리
                UserDTO.SignupResDTO signupResDTO = modelMapper.map(loggedInUser, UserDTO.SignupResDTO.class);
                return new ResponseEntity<>(
                        DefaultRes.res(HttpStatus.FORBIDDEN.value(), ResponseMessage.LIFESTYLE_TEST_NEEDED,
                                signupResDTO
                        ), HttpStatus.FORBIDDEN);
            }
            // 토큰 정보가 있으면 토큰 정보 전송 및 로그인 완료 처리
            UserDTO.LoginResDTO loginResDTO = modelMapper.map(loggedInUser, UserDTO.LoginResDTO.class);
            loginResDTO.setGrantType(tokens.getGrantType());
            loginResDTO.setAccessToken(tokens.getAccessToken());
            loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());
            loginResDTO.setRefreshToken(tokens.getRefreshToken());
            loginResDTO.setRefreshTokenExpiresIn(tokens.getRefreshTokenExpiresIn());

            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS,
                            loginResDTO
                    ), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("e = " + e);
            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.UNAUTHORIZED.value(), ResponseMessage.LOGIN_FAIL),
                    HttpStatus.UNAUTHORIZED
            );
        }
//        } catch (Exception e) {
//            System.out.println("e = " + e);
//            return new ResponseEntity<>(
//                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
    }

    @PostMapping(value = "/login/kakao")
    @ResponseBody
    public ResponseEntity<SocialLoginRes<?>> authKakao(@RequestBody Map<String, String> map, HttpServletResponse response) {
        try {
            String token= map.get("token");
            User user;
            try {
                user = userService.authKakao(token);
            } catch (Exception e) {
                if (e.getMessage().equals("유효하지 않은 토큰")) {
                    return new ResponseEntity<>(
                            SocialLoginRes.res(HttpStatus.UNAUTHORIZED.value(), ResponseMessage.UNAUTHORIZED_TOKEN, false),
                            HttpStatus.UNAUTHORIZED
                    );
                } else {
                    throw e;
                }
            }

            // 유저 정보가 있으면 로그인 처리
            if (user.getId() != null) {
                Map<String, Object> result = userService.login(user);
                User loggedInUser = (User) result.get("user");
                Tokens tokens;
                if ((tokens = (Tokens) result.get("tokens")) == null) {
                    // 토큰 정보가 없으면 라이프스타일 미완료 유저 -> Forbidden 처리
                    UserDTO.SignupResDTO signupResDTO = modelMapper.map(loggedInUser, UserDTO.SignupResDTO.class);
                    return new ResponseEntity<>(
                            SocialLoginRes.res(HttpStatus.FORBIDDEN.value(), ResponseMessage.LIFESTYLE_TEST_NEEDED,
                                    signupResDTO, false),
                            HttpStatus.FORBIDDEN);
                }
                // 토큰 정보가 있으면 토큰 정보 전송 및 로그인 완료 처리
                UserDTO.LoginResDTO loginResDTO = modelMapper.map(loggedInUser, UserDTO.LoginResDTO.class);
                loginResDTO.setGrantType(tokens.getGrantType());
                loginResDTO.setAccessToken(tokens.getAccessToken());
                loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());
                loginResDTO.setRefreshToken(tokens.getRefreshToken());
                loginResDTO.setRefreshTokenExpiresIn(tokens.getRefreshTokenExpiresIn());

                return new ResponseEntity<>(
                        SocialLoginRes.res(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS,
                                loginResDTO, true),
                        HttpStatus.OK
                );
            }
            // 유저 정보가 없으면 회원가입을 위해 기본 정보 보내주기
            return new ResponseEntity<>(
                    SocialLoginRes.res(HttpStatus.OK.value(), ResponseMessage.ADDITIONAL_INFO_REQUIRED,
                            modelMapper.map(user, signupReqDTO.class), false),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            System.out.println("e = " + e.getMessage());
            return new ResponseEntity<>(
                    SocialLoginRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR, false),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping(value = "/login/naver")
    @ResponseBody
    public ResponseEntity<DefaultRes<?>> authNaver(@RequestBody Map<String, String> map,
                                                   HttpServletResponse response) {
        try {
            String token = map.get("token");
            User user;
            try {
                user = userService.authNaver(token);
            } catch (Exception e) {
                if (e.getMessage().equals("유효하지 않은 토큰")) {
                    return new ResponseEntity<>(
                            SocialLoginRes.res(HttpStatus.UNAUTHORIZED.value(), ResponseMessage.UNAUTHORIZED_TOKEN, false),
                            HttpStatus.UNAUTHORIZED
                    );
                } else {
                    throw e;
                }
            }

            // 유저 정보가 있으면 로그인 처리
            if (user.getId() != null) {
                Map<String, Object> result = userService.login(user);
                User loggedInUser = (User) result.get("user");
                Tokens tokens;
                if ((tokens = (Tokens) result.get("tokens")) == null) {
                    // 토큰 정보가 없으면 라이프스타일 미완료 유저 -> Forbidden 처리
                    UserDTO.SignupResDTO signupResDTO = modelMapper.map(loggedInUser, UserDTO.SignupResDTO.class);
                    return new ResponseEntity<>(
                            SocialLoginRes.res(HttpStatus.FORBIDDEN.value(), ResponseMessage.LIFESTYLE_TEST_NEEDED,
                                    signupResDTO, false),
                            HttpStatus.FORBIDDEN);
                }
                // 토큰 정보가 있으면 토큰 정보 전송 및 로그인 완료 처리
                UserDTO.LoginResDTO loginResDTO = modelMapper.map(loggedInUser, UserDTO.LoginResDTO.class);
                loginResDTO.setGrantType(tokens.getGrantType());
                loginResDTO.setAccessToken(tokens.getAccessToken());
                loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());
                loginResDTO.setRefreshToken(tokens.getRefreshToken());
                loginResDTO.setRefreshTokenExpiresIn(tokens.getRefreshTokenExpiresIn());

                return new ResponseEntity<>(
                        SocialLoginRes.res(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS,
                                loginResDTO, true),
                        HttpStatus.OK
                );
            }
            // 유저 정보가 없으면 회원가입을 위해 기본 정보 보내주기
            return new ResponseEntity<>(
                    SocialLoginRes.res(HttpStatus.OK.value(), ResponseMessage.ADDITIONAL_INFO_REQUIRED,
                            modelMapper.map(user, signupReqDTO.class), false),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            System.out.println("e = " + e.getMessage());
            return new ResponseEntity<>(
                    SocialLoginRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR, false),
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

    @PatchMapping(value = "/password")
    public ResponseEntity<DefaultRes<?>> updatePassword(@RequestBody UpdatePWReqDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);
            int result = userService.updatePassword(user);

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
