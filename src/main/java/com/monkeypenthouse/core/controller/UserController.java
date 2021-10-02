package com.monkeypenthouse.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
