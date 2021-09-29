package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.dao.*;
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

@RestController
@RequestMapping("/user/all/")
@Log4j2
@RequiredArgsConstructor
public class UserForAllController {

    private final UserService userService;
    private final RoomService roomService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> signUp(@RequestBody UserDTO.LocalSignUpDTO userDTO) {
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

            return new ResponseEntity<>(room.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/check-id-duplication")
    public ResponseEntity<String> checkIdDuplicate(@RequestParam("email") String email) {
        try {
            boolean exist = userService.checkIdDuplicate(email);
            if (exist) {
                return ResponseEntity.ok("YES");
            } else {
                return  ResponseEntity.ok("NO");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }

    @GetMapping(value = "/check-name-duplication")
    public ResponseEntity<String> checkNameDuplicate(@RequestParam("name") String name) {
        try {
            boolean exist = userService.checkNameDuplicate(name);
            if (exist) {
                return ResponseEntity.ok("YES");
            } else {
                return  ResponseEntity.ok("NO");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}
