package com.omo.backend.global.security;

import com.omo.backend.domain.auth.exception.AuthErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String BLACKLIST_PREFIX = "BL:";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = resolveToken(request);

        try {
            // 추출된 토큰이 있고, 형식이 올바른 경우에만 검증 진행
            if (StringUtils.hasText(jwtToken)) {
                if (jwtTokenProvider.isValid(jwtToken)) {

                    String isLogout = redisTemplate.opsForValue().get(BLACKLIST_PREFIX + jwtToken);

                    if (StringUtils.hasText(isLogout)) {
                        request.setAttribute("exception", AuthErrorCode.LOGOUT_TOKEN);
                    } else {
                        Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("[JwtTokenFilter] 토큰 만료: {}", e.getMessage());
            request.setAttribute("exception", AuthErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException e) {
            log.warn("[JwtTokenFilter] 잘못된 토큰 서명: {}", e.getMessage());
            request.setAttribute("exception", AuthErrorCode.INVALID_TOKEN_SIGNATURE);
        } catch (Exception e) {
            log.error("[JwtTokenFilter] 알 수 없는 토큰 에러: ", e);
            request.setAttribute("exception", AuthErrorCode.TOKEN_INVALID);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        if (StringUtils.hasText(bearerToken)) {
            // 토큰은 있는데 Bearer로 시작하지 않는 경우 (형식 오류)
            request.setAttribute("exception", AuthErrorCode.INVALID_TOKEN_FORMAT);
        }
        return null;
    }
}
