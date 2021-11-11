package com.monkeypenthouse.core.config;

import com.monkeypenthouse.core.security.JwtAccessDeniedHandler;
import com.monkeypenthouse.core.security.JwtAuthenticationEntryPoint;
import com.monkeypenthouse.core.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.Authenticator;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@Log4j2
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                // 모두에게 접근 가능한 요청 설정
                .antMatchers("/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // csrf 비활성
        http.httpBasic().disable()
        .cors().configurationSource(corsConfigurationSource())

        .and()
        .csrf().disable()

        // exception handling할 때 커스텀 클래스 추가
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .accessDeniedHandler(jwtAccessDeniedHandler)

        // 시큐리티 세션 설정을 Stateless로 설정
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // 로그인, 회원가입 API는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
        .and()
        .authorizeRequests()
        .antMatchers("/").permitAll()
        .antMatchers("/user/all/*").permitAll()
        .antMatchers("/user/all/*/*").permitAll()
        .anyRequest().authenticated()

        // JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스를 적용
        .and()
        .apply(new JwtSecurityConfig(tokenProvider));

        http.formLogin();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("localhost:8080");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
