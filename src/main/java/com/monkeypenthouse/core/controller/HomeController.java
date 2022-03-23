package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.monkeypenthouse.core.common.DefaultRes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final CommonResponseMaker commonResponseMaker;

    @RequestMapping("/")
    public CommonResponse<Void> home() {

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }

}
