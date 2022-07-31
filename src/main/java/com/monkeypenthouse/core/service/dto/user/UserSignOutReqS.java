package com.monkeypenthouse.core.service.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UserSignOutReqS {
    private final String signOutReason;
}
