package com.omo.backend.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class OAuthResponseDTO {

    // Google 인증 화면으로 이동할 URL
    public record GoogleAuthorizationUrlDTO(
            @Schema(description = "Google OAuth 인증 URL", example = "https://accounts.google.com/o/oauth2/v2/auth?client_id=...")
            String authorizationUrl
    ) {}

    // Google OAuth 액세스 토큰 발급 결과
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GoogleTokenDTO(
            @JsonProperty("access_token")
            String accessToken
    ) {}

    // Google 사용자 정보 조회 결과
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GoogleUserInfoDTO(
            String sub,
            String email,
            @JsonProperty("email_verified")
            Boolean emailVerified,
            String name,
            String picture
    ) {}
}
