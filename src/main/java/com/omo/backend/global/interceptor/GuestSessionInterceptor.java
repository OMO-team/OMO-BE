package com.omo.backend.global.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GuestSessionInterceptor implements HandlerInterceptor {

    public static final String COOKIE_NAME = "__Host-guest_session_id";
    public static final String ATTR_NAME = "guestSessionId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String guestSessionId = extractCookie(request);
        if (guestSessionId == null) {
            guestSessionId = UUID.randomUUID().toString();
            ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, guestSessionId)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(Duration.ofDays(30))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        request.setAttribute(ATTR_NAME, guestSessionId);
        return true;
    }

    private String extractCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                String value = cookie.getValue();
                return isValidUuid(value) ? value : null; // 형식 안 맞으면 없는 것처럼 처리
            }
        }
        return null;
    }
    private boolean isValidUuid(String value) {
        if (value == null) return false;
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
