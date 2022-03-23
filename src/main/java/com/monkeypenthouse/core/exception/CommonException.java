package com.monkeypenthouse.core.exception;

import com.monkeypenthouse.core.constant.ResponseCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final ResponseCode code;

    public CommonException(ResponseCode code) {
        this.code = code;
    }
}
