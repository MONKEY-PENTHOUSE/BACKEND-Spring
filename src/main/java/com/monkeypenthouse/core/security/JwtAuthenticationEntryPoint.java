package com.monkeypenthouse.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 유저 정보 없이 접근하면 SC_UNAUTHORIZED(401) 응답
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        String exception = (String)request.getAttribute("exception");

        if (exception == null) {
            DefaultRes<?> responseObj = DefaultRes.res(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 회원입니다.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            String json = new ObjectMapper().writeValueAsString(responseObj);

            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(json);
            response.flushBuffer();
        } else {
            DefaultRes<?> responseObj = DefaultRes.res(HttpStatus.UNAUTHORIZED.value(), exception);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            String json = new ObjectMapper().writeValueAsString(responseObj);

            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(json);
            response.flushBuffer();
        }

    }
}
