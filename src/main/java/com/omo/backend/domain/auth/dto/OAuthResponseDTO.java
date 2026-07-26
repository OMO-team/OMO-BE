package com.omo.backend.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthResponseDTO {

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
