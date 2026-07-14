package com.omo.backend.domain.roadmap.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoadmapErrorCode implements BaseErrorCode {

    INVALID_STAY_MONTHS(HttpStatus.BAD_REQUEST, "ROADMAP400_1", "체류 기간은 1개월 이상이어야 합니다."),
    INVALID_DEPARTURE_DATE(HttpStatus.BAD_REQUEST, "ROADMAP400_2", "출발일은 오늘 이후여야 합니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
