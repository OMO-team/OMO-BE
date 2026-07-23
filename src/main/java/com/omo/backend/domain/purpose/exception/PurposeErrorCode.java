package com.omo.backend.domain.purpose.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PurposeErrorCode implements BaseErrorCode {
    INVALID_PURPOSE_TYPE(HttpStatus.BAD_REQUEST, "PURPOSE400_1", "유효하지 않은 목적입니다."),
    PURPOSE_NOT_FOUND(HttpStatus.NOT_FOUND, "PURPOSE404_1", "존재하지 않는 목적입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
