package com.monkeypenthouse.core.controller.dto.user;

import com.monkeypenthouse.core.service.dto.user.UserSignOutReqS;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSignOutReqI {
    private String signOutReason;

    public UserSignOutReqS toS() {
        return new UserSignOutReqS(signOutReason);
    }
}
