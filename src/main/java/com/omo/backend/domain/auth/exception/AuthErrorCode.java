package com.omo.backend.domain.auth.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    EMAIL_VERIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH400_1", "이메일 인증번호가 만료되었거나 존재하지 않습니다."),
    EMAIL_VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH400_2", "이메일 인증번호가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH400_3", "이메일 인증을 완료해 주세요."),
    EMAIL_VERIFICATION_ATTEMPT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AUTH429_1", "이메일 인증번호 입력 가능 횟수를 초과했습니다. 인증번호를 다시 발급해 주세요."),
    INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "AUTH401_1", "토큰 형식이 올바르지 않습니다. (Bearer 형식 필요)"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401_2", "토큰이 만료되었습니다. 다시 로그인하거나 토큰을 갱신해주세요."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH401_3", "토큰의 서명이 일치하지 않습니다. 변조된 토큰일 위험이 있습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH401_4", "유효하지 않은 토큰입니다. 다시 확인해주세요."),
    LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_5", "이미 로그아웃된 토큰입니다. 다시 로그인해 주세요."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500_1", "이메일 발송에 실패했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
