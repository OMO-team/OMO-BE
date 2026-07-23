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
    RESOURCE_TOPIC_INVALID(HttpStatus.BAD_REQUEST, "RESOURCE400_1", "유효하지 않은 topic 값입니다."),
    RESOURCE_TYPE_INVALID(HttpStatus.BAD_REQUEST, "RESOURCE400_2", "유효하지 않은 resourceType 값입니다."),
    COMPARE_ITEM_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "COMPARE400_1", "비교함에는 최대 3개까지만 담을 수 있습니다."),
    COMPARE_CITY_IDS_INVALID(HttpStatus.BAD_REQUEST, "COMPARE400_2", "비교할 도시는 2개 이상 3개 이하여야 합니다."),
    COMPARE_CITY_IDS_DUPLICATED(HttpStatus.BAD_REQUEST, "COMPARE400_3", "중복된 도시가 있습니다."),
    COMPARE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPARE404_1", "비교함에서 해당 도시를 찾을 수 없습니다."),
    COMPARE_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "COMPARE409_1", "이미 비교함에 담긴 도시입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}