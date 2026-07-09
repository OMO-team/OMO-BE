package com.omo.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Swagger 및 API 문서 관련 URL 배열
    private static final String[] SWAGGER_URLS = {
            "/api/v3/api-docs/**",
            "/api/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/swagger-ui/**",
            "/webjars/**",
            "/api/webjars/**"
    };

    // 인증 없이 접근할 수 있는 공개 API URL 배열
    private static final String[] PUBLIC_API_URLS = {
            "/api/v1/members/signup",
            "/auth/v1/email/**",
            "/api/v1/terms"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)  // csrf 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)  // Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)   // 로그인 폼 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_URLS).permitAll()
                        .requestMatchers(PUBLIC_API_URLS).permitAll()
                        .anyRequest().authenticated()
                );
        return httpSecurity.build();
    }
}
