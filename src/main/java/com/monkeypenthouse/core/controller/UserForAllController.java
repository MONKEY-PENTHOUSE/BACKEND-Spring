package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.common.SocialLoginRes;
import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.CheckUserResponseDTO;
import com.monkeypenthouse.core.dto.CommonResponse;
import com.monkeypenthouse.core.entity.*;
import com.monkeypenthouse.core.dto.UserDTO;
import com.monkeypenthouse.core.dto.UserDTO.*;
import com.monkeypenthouse.core.service.MessageService;
import com.monkeypenthouse.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Map;

@RestController
@RequestMapping("/user/all/")
@Log4j2
@RequiredArgsConstructor
@Validated
public class UserForAllController {

    private final UserService userService;
    private final MessageService messageService;
    private final ModelMapper modelMapper;
    private final CommonResponseMaker commonResponseMaker;

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<SignupResDTO> signUp(@RequestBody @Valid SignupReqDTO userDTO) throws Exception {

        final User user = modelMapper.map(userDTO, User.class);

        return commonResponseMaker.makeSucceedCommonResponse(modelMapper.map(userService.add(user), SignupResDTO.class));
    }

    /* 회원가입 테스트 용 */
    @DeleteMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse<Void> delete(@RequestParam("email") String email) throws Exception {
        userService.deleteByEmail(email);

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }


    @GetMapping(value = "/check-id-duplication")
    public CommonResponse<Boolean> checkIdDuplicate(@RequestParam("email") String email) throws Exception {

        return commonResponseMaker.makeSucceedCommonResponse(userService.checkEmailDuplicate(email));
    }

    @PostMapping(value = "/sms-auth")
    public CommonResponse<Void> smsAuth(@RequestBody Map<String, String> map) throws Exception {

        final String phoneNum = map.get("phoneNum");

        userService.checkPhoneNumDuplicate(phoneNum);
        messageService.sendSMS(phoneNum);

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }

    @PostMapping(value = "/check-sms-auth")
    public CommonResponse<Boolean> checkSmsAuth(@RequestBody Map<String, String> map) throws Exception {

        final String phoneNum = map.get("phoneNum");
        final String authNum = map.get("authNum");

        return commonResponseMaker.makeSucceedCommonResponse(messageService.checkAuthNum(phoneNum, authNum));
    }

    @PatchMapping(value = "/life-style")
    public CommonResponse<Void> updateLifeStyle(@RequestBody @Valid UpdateLSReqDTO userDTO) throws Exception {

        final User user = modelMapper.map(userDTO, User.class);

        userService.updateLifeStyle(user);

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public CommonResponse<UserDTO.LoginResDTO> loginLocal(@RequestBody @Valid LoginReqDTO userDTO) throws Exception {
        User user = modelMapper.map(userDTO, User.class);

        Map<String, Object> map = userService.login(user);
        User loggedInUser = (User) map.get("user");
        Tokens tokens = (Tokens) map.get("tokens");

        UserDTO.LoginResDTO loginResDTO = modelMapper.map(loggedInUser, UserDTO.LoginResDTO.class);
        loginResDTO.setGrantType(tokens.getGrantType());
        loginResDTO.setAccessToken(tokens.getAccessToken());
        loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());
        loginResDTO.setRefreshToken(tokens.getRefreshToken());
        loginResDTO.setRefreshTokenExpiresIn(tokens.getRefreshTokenExpiresIn());

        return commonResponseMaker.makeSucceedCommonResponse(loginResDTO);
    }

    @PostMapping(value = "/login/kakao")
    @ResponseBody
    public CommonResponse<UserDTO.LoginResDTO> authKakao(@RequestBody Map<String, String> map) throws Exception {

        String token = map.get("token");
        User user = userService.authKakao(token);
        // 유저 정보가 있으면 로그인 처리
        Map<String, Object> result = userService.login(user);
        User loggedInUser = (User) result.get("user");
        Tokens tokens = (Tokens) result.get("tokens");

        // 토큰 정보가 있으면 토큰 정보 전송 및 로그인 완료 처리
        UserDTO.LoginResDTO loginResDTO = modelMapper.map(loggedInUser, UserDTO.LoginResDTO.class);
        loginResDTO.setGrantType(tokens.getGrantType());
        loginResDTO.setAccessToken(tokens.getAccessToken());
        loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());
        loginResDTO.setRefreshToken(tokens.getRefreshToken());
        loginResDTO.setRefreshTokenExpiresIn(tokens.getRefreshTokenExpiresIn());

        return commonResponseMaker.makeSucceedCommonResponse(loginResDTO);
    }

    @PostMapping(value = "/login/naver")
    @ResponseBody
    public CommonResponse<UserDTO.LoginResDTO> authNaver(@RequestBody Map<String, String> map) throws Exception {
        String token = map.get("token");
        User user = userService.authNaver(token);
        // 유저 정보가 있으면 로그인 처리
        Map<String, Object> result = userService.login(user);
        User loggedInUser = (User) result.get("user");
        Tokens tokens = (Tokens) result.get("tokens");

        // 토큰 정보가 있으면 토큰 정보 전송 및 로그인 완료 처리
        UserDTO.LoginResDTO loginResDTO = modelMapper.map(loggedInUser, UserDTO.LoginResDTO.class);
        loginResDTO.setGrantType(tokens.getGrantType());
        loginResDTO.setAccessToken(tokens.getAccessToken());
        loginResDTO.setAccessTokenExpiresIn(tokens.getAccessTokenExpiresIn());
        loginResDTO.setRefreshToken(tokens.getRefreshToken());
        loginResDTO.setRefreshTokenExpiresIn(tokens.getRefreshTokenExpiresIn());

        return commonResponseMaker.makeSucceedCommonResponse(loginResDTO);
    }

    @GetMapping(value = "/find-email")
    public CommonResponse<FindEmailResDTO> findEmail(@RequestParam("phoneNum") @Pattern(regexp = "^\\d{9,11}$") String phoneNum) throws Exception {

        return commonResponseMaker.makeSucceedCommonResponse(
                modelMapper.map(userService.findEmail(phoneNum), FindEmailResDTO.class));
    }

    @GetMapping(value = "/check-user")
    public CommonResponse<CheckUserResponseDTO> checkUser(@RequestParam("phoneNum") @Pattern(regexp = "^\\d{9,11}$") String phoneNum,
                                                          @RequestParam("email") @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$") String email) throws Exception {

        final CheckUserResponseDTO responseDTO = CheckUserResponseDTO.of(userService.checkUser(phoneNum, email));

        return commonResponseMaker.makeSucceedCommonResponse(responseDTO);
    }

}
