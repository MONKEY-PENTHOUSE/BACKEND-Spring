package com.monkeypenthouse.core.loggging;

import com.monkeypenthouse.core.security.PrincipalDetails;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import static net.logstash.logback.marker.Markers.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Aspect
public class LoggerAspect {

    @AfterReturning(value = "bean(*Controller)")
    public void methodLogger(JoinPoint joinPoint) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        ServletRequestAttributes contextHolder = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(contextHolder).getRequest();
        HttpServletResponse response = contextHolder.getResponse();

        if (request.getRequestURI().equals("/")) return;

        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String httpMethodName = request.getMethod();

        this.setPayLoad(joinPoint, pathVariables);

        Object principal1 = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal1 instanceof UserDetails) {
            UserDetails principal = (UserDetails) principal1;
            String authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            Map<String, String> map = new HashMap<>();
            map.put("parameters", pathVariables.toString());
            map.put("http_method", httpMethodName);
            map.put("request_url", request.getRequestURI());
            map.put("user_id", principal.getUsername());
            map.put("user_type", authorities);
            map.put("http.response.status_code", String.valueOf(response == null ? null : response.getStatus()));
            logger.info(appendEntries(map), "");
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("parameters", pathVariables.toString());
            map.put("http_method", httpMethodName);
            map.put("request_url", request.getRequestURI());
            map.put("user_id", null);
            map.put("user_type", null);
            map.put("http.response.status_code", String.valueOf(response == null ? null : response.getStatus()));
            logger.info(appendEntries(map), "");
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
