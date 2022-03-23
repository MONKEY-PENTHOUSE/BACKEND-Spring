package com.monkeypenthouse.core.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final CommonResponseMaker commonResponseMaker;

    // CommonException 통합
    @ExceptionHandler({CommonException.class})
    protected ResponseEntity<CommonResponseMaker.CommonResponse> handleCommonException(final CommonException e) {

        return new ResponseEntity<>(
                commonResponseMaker.makeFailedCommonResponse(e.getCode()),
                e.getCode().getHttpStatus()
        );
    }

    // 중복된 데이터 추가 요청 시
    @ExceptionHandler({DataIntegrityViolationException.class})
    protected ResponseEntity<CommonResponseMaker.CommonResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

        return new ResponseEntity<>(
                commonResponseMaker.makeFailedCommonResponse(ResponseCode.DATA_INTEGRITY_VIOLATED),
                ResponseCode.DATA_INTEGRITY_VIOLATED.getHttpStatus()
        );
    }

    // 로컬 로그인 실패 시 (이메일, 비번 오류)
    @ExceptionHandler({AuthenticationException.class})
    protected ResponseEntity<CommonResponseMaker.CommonResponse> handleAuthenticationException(AuthenticationException e) {

        return new ResponseEntity<>(
                commonResponseMaker.makeFailedCommonResponse(ResponseCode.AUTHENTICATION_FAILED),
                ResponseCode.AUTHENTICATION_FAILED.getHttpStatus()
        );
    }

    // json 파싱 실패시
    @ExceptionHandler({HttpMessageNotReadableException.class})
    protected ResponseEntity<CommonResponseMaker.CommonResponse> handleHttpMessageNotReadableException(JsonMappingException e) {

        return new ResponseEntity<>(
                commonResponseMaker.makeFailedCommonResponse(ResponseCode.HTTP_MESSAGE_NOT_READABLE),
                ResponseCode.HTTP_MESSAGE_NOT_READABLE.getHttpStatus()
        );
    }

    // @valid 유효성 검사 실패시 (RequestBody)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<CommonResponseMaker.CommonResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        return new ResponseEntity<>(
                commonResponseMaker.makeFailedCommonResponse(ResponseCode.METHOD_ARGUMENT_NOT_VALID),
                ResponseCode.METHOD_ARGUMENT_NOT_VALID.getHttpStatus()
        );
    }

    // @valid 유효성 검사 실패시 (RequestParam, PathVariable)
    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<CommonResponseMaker.CommonResponse> handleMethodArgumentNotValidException(ConstraintViolationException e) {

        return new ResponseEntity<>(
                commonResponseMaker.makeFailedCommonResponse(ResponseCode.CONSTRAINT_VIOLATED),
                ResponseCode.CONSTRAINT_VIOLATED.getHttpStatus()
        );
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseMaker.CommonResponse> handleAll(final Exception e) {

        return new ResponseEntity<>(
                commonResponseMaker.makeFailedCommonResponse(ResponseCode.INTERNAL_SERVER_ERROR),
                ResponseCode.INTERNAL_SERVER_ERROR.getHttpStatus()
        );
    }
}
