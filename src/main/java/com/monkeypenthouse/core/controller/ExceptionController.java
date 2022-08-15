package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.component.CommonResponseMaker.CommonResponseEntity;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.exception.*;
import com.monkeypenthouse.core.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.logstash.logback.marker.Markers.appendEntries;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final CommonResponseMaker commonResponseMaker;

    // CommonException 통합
    @ExceptionHandler({CommonException.class})
    protected CommonResponseEntity handleCommonException(final CommonException e) {

        logError(e.getCode());
        return e.getData() == null ?
                commonResponseMaker.makeCommonResponse(e.getCode())
                : commonResponseMaker.makeCommonResponse(e.getData(), e.getCode());
    }

    // 중복된 데이터 추가 요청 시
    @ExceptionHandler({DataIntegrityViolationException.class})
    protected CommonResponseEntity handleDataIntegrityViolationException(DataIntegrityViolationException e) {

        logError(ResponseCode.DATA_INTEGRITY_VIOLATED);
        return commonResponseMaker.makeCommonResponse(ResponseCode.DATA_INTEGRITY_VIOLATED);
    }

    // 로컬 로그인 실패 시 (이메일, 비번 오류)
    @ExceptionHandler({AuthenticationException.class})
    protected CommonResponseEntity handleAuthenticationException(AuthenticationException e) {

        logError(ResponseCode.AUTHENTICATION_FAILED);
        return commonResponseMaker.makeCommonResponse(ResponseCode.AUTHENTICATION_FAILED);
    }

    // multipart/form-data or json 파싱 실패시
    @ExceptionHandler({HttpMessageNotReadableException.class, BindException.class})
    protected CommonResponseEntity handleHttpMessageNotReadableException(Exception e) {

        logError(ResponseCode.HTTP_MESSAGE_NOT_READABLE);
        return commonResponseMaker.makeCommonResponse(ResponseCode.HTTP_MESSAGE_NOT_READABLE);
    }

    // @valid 유효성 검사 실패시 (RequestBody)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected CommonResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        logError(ResponseCode.METHOD_ARGUMENT_NOT_VALID);
        return commonResponseMaker.makeCommonResponse(ResponseCode.METHOD_ARGUMENT_NOT_VALID);
    }

    // @valid 유효성 검사 실패시 (RequestParam, PathVariable)
    @ExceptionHandler({ConstraintViolationException.class})
    protected CommonResponseEntity handleMethodArgumentNotValidException(ConstraintViolationException e) {

        logError(ResponseCode.CONSTRAINT_VIOLATED);
        return commonResponseMaker.makeCommonResponse(ResponseCode.CONSTRAINT_VIOLATED);
    }

    // @valid 유효성 검사 실패시 (RequestParam, PathVariable)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    protected CommonResponseEntity handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {

        logError(ResponseCode.MISSING_PARAMETER);
        return commonResponseMaker.makeCommonResponse(ResponseCode.MISSING_PARAMETER);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public CommonResponseEntity handleAll(final Exception e) {
        e.printStackTrace();

        logError(ResponseCode.INTERNAL_SERVER_ERROR);
        return commonResponseMaker.makeCommonResponse(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    public void logError(ResponseCode code) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        ServletRequestAttributes contextHolder = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(contextHolder).getRequest();

        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String httpMethodName = request.getMethod();

        Object principal1 = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal1 instanceof PrincipalDetails) {
            PrincipalDetails principal = (PrincipalDetails) principal1;
            String authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            Map<String, String> map = new HashMap<>();
            map.put("parameters", pathVariables.toString());
            map.put("http_method", httpMethodName);
            map.put("request_url", request.getRequestURI());
            map.put("user_id", principal.getUsername());
            map.put("user_type", authorities);
            map.put("http.response.status_code", String.valueOf(code));
            logger.error(appendEntries(map), code.getMessage());
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("parameters", pathVariables.toString());
            map.put("http_method", httpMethodName);
            map.put("request_url", request.getRequestURI());
            map.put("user_id", null);
            map.put("user_type", null);
            map.put("http.response.status_code", String.valueOf(code));
            logger.error(appendEntries(map), code.getMessage());
        }
    }
}
