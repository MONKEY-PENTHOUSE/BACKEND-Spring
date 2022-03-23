package com.monkeypenthouse.core.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.SocialLoginRes;
import com.monkeypenthouse.core.exception.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;

    // CommonException 통합
    @ExceptionHandler({CommonException.class})
    protected ResponseEntity<DefaultRes<?>> handleCommonException(final CommonException e) {
        return new ResponseEntity<>(
                DefaultRes.res(
                        e.getCode().getHttpStatus().value(),
                        e.getCode().getMessage()),
                e.getCode().getHttpStatus()
        );
    }

    // 중복된 데이터 추가 요청 시
    @ExceptionHandler({ DataIntegrityViolationException.class })
    protected ResponseEntity<DefaultRes<?>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    // 로컬 로그인 실패 시 (이메일, 비번 오류)
    @ExceptionHandler({ AuthenticationException.class })
    protected ResponseEntity<DefaultRes<?>> handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // json 파싱 실패시
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    protected ResponseEntity<DefaultRes<?>> handleHttpMessageNotReadableException(JsonMappingException e) {
        List<String> fieldErrors = new ArrayList<>();
        for (JsonMappingException.Reference fieldError : e.getPath()) {
            fieldErrors.add(fieldError.getFieldName());
        }
        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.BAD_REQUEST.value(),
                        "정보가 유효하지 않습니다. (json 파싱 오류)",
                        fieldErrors),
                HttpStatus.BAD_REQUEST
        );
    }

    // @valid 유효성 검사 실패시 (RequestBody)
    @ExceptionHandler({ MethodArgumentNotValidException.class})
    protected ResponseEntity<DefaultRes<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());


        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.BAD_REQUEST.value(),
                        "정보가 유효하지 않습니다. (형식을 벗어난 값)",
                        errors),
                HttpStatus.BAD_REQUEST
        );

    }

    // @valid 유효성 검사 실패시 (RequestParam, PathVariable)
    @ExceptionHandler({ ConstraintViolationException.class})
    protected ResponseEntity<DefaultRes<?>> handleMethodArgumentNotValidException(ConstraintViolationException e) {

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> errors = constraintViolations.stream()
                .map( cv -> cv == null ? "null" : cv.getPropertyPath().toString())
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.BAD_REQUEST.value(),
                        "정보가 유효하지 않습니다. (형식을 벗어난 값)",
                        errors),
                HttpStatus.BAD_REQUEST
        );

    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultRes<?>> handleAll(final Exception e) {
        System.out.println("e = " + e);
        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "서버의 내부적인 오류로 인해 문제가 발생하였습니다."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
