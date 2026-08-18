package com.omo.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.code.GeneralErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 필터에서 넘겨준 에러 코드가 있는지 확인
        AuthErrorCode exceptionCode = (AuthErrorCode) request.getAttribute("exception");

        // 만약 넘어온 에러 코드가 없다면 기본 권한 없음 에러 사용
        BaseErrorCode errorCode = (exceptionCode != null) ? exceptionCode : GeneralErrorCode.UNAUTHORIZED;

        log.warn("[AuthenticationEntryPoint] 인증 실패: {} | URI: {}", errorCode.getMessage(), request.getRequestURI());

        ApiResponse<Void> errorResponse = ApiResponse.onFailure(errorCode, null);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
