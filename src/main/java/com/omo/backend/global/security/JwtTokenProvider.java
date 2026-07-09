package com.omo.backend.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration}") Duration accessTokenMinutes,
            @Value("${jwt.refresh-token-expiration}") Duration refreshTokenDays
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);

        // 단위를 밀리초로 통일하여 저장
        this.accessTokenExpirationMs = accessTokenMinutes.toMillis();
        this.refreshTokenExpirationMs = refreshTokenDays.toMillis();
    }

    // Access Token 생성
    public String createAccessToken(Long memberId, String email) {
        return createToken(memberId, email, accessTokenExpirationMs);
    }

    // Refresh Token 생성
    public String createRefreshToken(Long memberId, String email) {
        return createToken(memberId, email, refreshTokenExpirationMs);
    }

    // 토큰 생성
    private String createToken(Long memberId, String email, long validityMs) {
        Date now = new Date();

        return Jwts.builder()
                .claims(Map.of(
                        "memberId", memberId,
                        "email", email
                ))
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validityMs))
                .signWith(signingKey, Jwts.SIG.HS512)
                .compact();
    }

    // 토큰 유효성 검증 (예외는 JwtTokenFilter가 처리)
    public boolean isValid(String jwtToken) {
        Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(jwtToken);
        return true;
    }

    // 토큰에서 Authentication 객체 추출
    public Authentication getAuthentication(String jwtToken) {
        Claims claims = getClaims(jwtToken);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        Number memberId = claims.get("memberId", Number.class);
        CustomUserDetails userDetails = CustomUserDetails.of(memberId.longValue(), claims.getSubject(), authorities);

        return new UsernamePasswordAuthenticationToken(userDetails, jwtToken, authorities);
    }

    // 토큰에서 Claims(Payload) 추출 (내부 전용)
    private Claims getClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }
}
