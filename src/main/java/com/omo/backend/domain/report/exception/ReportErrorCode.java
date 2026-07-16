package com.omo.backend.domain.report.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {

    CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "CITY404_1", "도시를 찾을 수 없습니다."),
    AI_REPORT_QUERY_EMPTY(HttpStatus.BAD_REQUEST, "AIREPORT400_1", "질문을 입력해주세요."),
    RESOURCE_TOPIC_INVALID(HttpStatus.BAD_REQUEST, "RESOURCE400_1", "유효하지 않은 topic 값입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}