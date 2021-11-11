package com.monkeypenthouse.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<DefaultRes<?>> home() {
        return new ResponseEntity<>(
                DefaultRes.res(HttpStatus.OK.value(), "hello world!"),
                HttpStatus.OK
        );
    }

}
