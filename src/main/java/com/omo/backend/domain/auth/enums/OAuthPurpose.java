package com.omo.backend.domain.auth.enums;

public enum OAuthPurpose {

    SIGNUP,  // 약관 동의 후 Google 회원가입
    LOGIN,   // Google 로그인
    LINK     // 로그인 회원의 Google 계정 연결
}
