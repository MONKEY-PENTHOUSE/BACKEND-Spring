package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.Room;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dao.UserDTO.*;
import com.monkeypenthouse.core.dao.Authority;
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
@RequestMapping("/user/")
@Log4j2
@RequiredArgsConstructor
public class UserController {



//    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> getMyUserInfo() {
//        try {
//            MyUserDTO myUser = modelMapper.map(userService.getMyInfo(), MyUserDTO.class);
//            return ResponseEntity.ok(myUser);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(e.toString());
//        }
//    }



}
