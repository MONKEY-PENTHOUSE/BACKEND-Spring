package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    // 500
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<DefaultRes<?>> handleAll(final Exception ex) {
        return new ResponseEntity<>(
                DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
