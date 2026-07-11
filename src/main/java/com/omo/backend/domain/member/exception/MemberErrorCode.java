package com.omo.backend.domain.member.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER400_1", "이미 존재하는 이메일입니다."),
    INVALID_AGREED_TERMS(HttpStatus.BAD_REQUEST, "MEMBER400_2", "유효하지 않은 약관 동의 정보입니다."),
    REQUIRED_TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "MEMBER400_3", "필수 약관에 모두 동의해야 합니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
