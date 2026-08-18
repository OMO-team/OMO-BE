package com.omo.backend.domain.country.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CountryErrorCode implements BaseErrorCode {
;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
