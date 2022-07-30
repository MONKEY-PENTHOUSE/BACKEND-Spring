package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.component.CommonResponseMaker.CommonResponseEntity;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.controller.dto.purchase.PurchaseCreateReqI;
import com.monkeypenthouse.core.controller.dto.user.UserSignOutReqI;
import com.monkeypenthouse.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public CommonResponseEntity getMyUserInfo() {
        return commonResponseMaker.makeCommonResponse(userService.getMyInfo().toI(), ResponseCode.SUCCESS);
    }

    @PostMapping("/reissue")
    public CommonResponseEntity reissue(@RequestHeader("Authorization") String refreshToken) {

        return commonResponseMaker.makeCommonResponse(userService.reissue(refreshToken).toI(),
                ResponseCode.SUCCESS);
    }

    @PostMapping("/logout")
    public CommonResponseEntity logout() throws Exception {
        userService.logout();
        return commonResponseMaker.makeCommonResponse(ResponseCode.SUCCESS);
    }

    @PostMapping("/signOut")
    public CommonResponseEntity signOut(@AuthenticationPrincipal final UserDetails userDetails,
                                        @RequestBody final UserSignOutReqI request) {
        userService.signOut(userDetails, request.toS());
        return commonResponseMaker.makeCommonResponse(ResponseCode.SUCCESS);
    }
}
