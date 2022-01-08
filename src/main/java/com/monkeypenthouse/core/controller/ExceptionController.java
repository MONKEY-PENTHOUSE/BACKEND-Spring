package com.monkeypenthouse.core.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionController {

    // json 파싱 실패시
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    protected ResponseEntity<DefaultRes<?>> handleMethodArgumentNotValidException(JsonMappingException e) {
        List<String> fieldErrors = new ArrayList<>();
        for (JsonMappingException.Reference fieldError : e.getPath()) {
            fieldErrors.add(fieldError.getFieldName());
        }
        return new ResponseEntity<>(
                DefaultRes.res(HttpStatus.BAD_REQUEST.value(), ResponseMessage.JSON_PARSING_ERROR, fieldErrors),
                HttpStatus.BAD_REQUEST
        );
    }

    // @valid 유효성 검사 실패시
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<DefaultRes<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> fieldErrors = new ArrayList<>();
            BindingResult bindingResult = e.getBindingResult();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                fieldErrors.add(fieldError.getField());
            }

            return new ResponseEntity<>(
                    DefaultRes.res(HttpStatus.BAD_REQUEST.value(), ResponseMessage.INVALID_INPUTS, fieldErrors),
                    HttpStatus.BAD_REQUEST
            );

    }

//    // 예상가능한 Exception 처리
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<DefaultRes<?>> handleExpectedException(final Exception e) {
//        System.out.println("e = " + e);
//        return new ResponseEntity<>(
//                DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
//                HttpStatus.INTERNAL_SERVER_ERROR
//        );
//    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultRes<?>> handleAll(final Exception e) {
        System.out.println("e = " + e);
        return new ResponseEntity<>(
                DefaultRes.res(HttpStatus.INTERNAL_SERVER_ERROR.value(), ResponseMessage.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
