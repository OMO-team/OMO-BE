package com.omo.backend.domain.city.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CityErrorCode implements BaseErrorCode {

    CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "CITY404_1", "존재하지 않는 도시입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
