package com.omo.backend.domain.city.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CityErrorCode implements BaseErrorCode {

    CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "CITY404_1", "존재하지 않는 도시입니다."),
    INVALID_DIFFICULTY(HttpStatus.BAD_REQUEST, "CITY400_1", "유효하지 않은 난이도 값입니다. (EASY/NORMAL/HARD 중 선택)"),
    INVALID_STAY_DURATION(HttpStatus.BAD_REQUEST, "CITY400_2", "유효하지 않은 체류기간 값입니다. (SHORT/MEDIUM/LONG/VERY_LONG 중 선택)"),
    INVALID_KEYWORD_LENGTH(HttpStatus.BAD_REQUEST, "CITY400_3", "키워드는 50자 이하이어야 합니다."),
    INVALID_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "CITY400_4", "숫자 형식이 올바르지 않습니다.")
;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
