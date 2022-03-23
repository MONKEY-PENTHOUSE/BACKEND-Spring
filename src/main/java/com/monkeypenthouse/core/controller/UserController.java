package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.TokenDTO;
import com.monkeypenthouse.core.dto.UserDTO.*;
import com.monkeypenthouse.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/")
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final CommonResponseMaker commonResponseMaker;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/me")
    public CommonResponseMaker.CommonResponse<MyUserResDTO> getMyUserInfo() throws Exception {
            MyUserResDTO myUser = modelMapper.map(userService.getMyInfo(), MyUserResDTO.class);
        return commonResponseMaker.makeSucceedCommonResponse(myUser);
    }

    @PostMapping("/reissue")
    public CommonResponseMaker.CommonResponse<TokenDTO.ReissueResDTO> reissue(@RequestHeader("Authorization") String refreshToken) throws Exception {

        return commonResponseMaker.makeSucceedCommonResponse(
                modelMapper.map(userService.reissue(refreshToken), TokenDTO.ReissueResDTO.class));
    }

    @PostMapping("/logout")
    public CommonResponseMaker.CommonResponse<Void> logout() throws Exception {

        userService.logout();

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }
}
