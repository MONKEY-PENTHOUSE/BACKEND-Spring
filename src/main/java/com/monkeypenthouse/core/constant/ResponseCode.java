package com.monkeypenthouse.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS("Success", HttpStatus.OK);

    private final String message;
    private final HttpStatus httpStatus;
}
