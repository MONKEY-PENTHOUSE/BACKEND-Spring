package com.monkeypenthouse.core.loggging;

import com.monkeypenthouse.core.security.PrincipalDetails;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Aspect
public class LoggerAspect {

    @Before(value = "bean(*Controller")
    public void logMetaDataInit(JoinPoint joinPoint) {
        MDC.clear();
    }

    @AfterThrowing(value = "bean(*Controller)", throwing = "e")
    public void methodLogger(JoinPoint joinPoint, Throwable e) throws Throwable {
        ServletRequestAttributes contextHolder = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(contextHolder).getRequest();
        HttpServletResponse response = contextHolder.getResponse();

        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String httpMethodName = request.getMethod();

        this.setPayLoad(joinPoint, pathVariables);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principal;
        if (authentication != null) {
            principal = (PrincipalDetails) authentication.getPrincipal();
            String authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            MDC.put("parameters", pathVariables.toString());
            MDC.put("http_method", httpMethodName);
            MDC.put("request_url", request.getRequestURI());
            MDC.put("user_id", principal.getUsername());
            MDC.put("user_type", authorities);
            MDC.put("http.response.status_code", String.valueOf(response == null ? null : response.getStatus()));
        } else {
            MDC.put("parameters", pathVariables.toString());
            MDC.put("http_method", httpMethodName);
            MDC.put("request_url", request.getRequestURI());
            MDC.put("user_id", null);
            MDC.put("user_type", null);
            MDC.put("http.response.status_code", String.valueOf(response == null ? null : response.getStatus()));
        }


    }

    private void setPayLoad(JoinPoint joinPoint, Map<String, String> payloadMap) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();

        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            String parameterName = signature.getParameterNames()[i];
            if (joinPoint.getArgs()[i] != null) {
                payloadMap.put(parameterName, joinPoint.getArgs()[i].toString());
            }
        }
    }
}
