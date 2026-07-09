package com.omo.backend.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
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
    public String createAccessToken(String email) {
        return createToken(email, accessTokenExpirationMs);
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        return createToken(email, refreshTokenExpirationMs);
    }

    private String createToken(String email, long validityMs) {
        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .claims(Map.of("email", email))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validityMs))
                .signWith(signingKey, Jwts.SIG.HS512)
                .compact();
    }
}
