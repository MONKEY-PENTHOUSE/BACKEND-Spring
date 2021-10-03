package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.dao.*;
import com.monkeypenthouse.core.dto.RoomDTO.*;
import com.monkeypenthouse.core.dto.UserDTO;
import com.monkeypenthouse.core.service.MessageService;
import com.monkeypenthouse.core.service.RoomService;
import com.monkeypenthouse.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<DefaultRes<?>> signUp(@RequestBody UserDTO.LocalSignUpDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);

            // 비밀번호 암호화
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // loginType : LOCAL
            user.setLoginType(LoginType.LOCAL);
            // userRole : USER
            user.setAuthority(Authority.USER);

            log.info(user);
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
    public ResponseEntity<Object> checkSmsAuth(@RequestBody Map<String, String> map) {
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
                    DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.SEND_SMS_FAIL),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
