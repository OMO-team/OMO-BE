package com.omo.backend.domain.aisearch.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiSearchErrorCode implements BaseErrorCode {

    AI_SEARCH_QUERY_REQUIRED(HttpStatus.BAD_REQUEST, "AI400_1", "검색 쿼리 텍스트는 필수 항목입니다."),
    AI_SESSION_NOT_FOUND(HttpStatus.BAD_REQUEST, "AI400_2", "해당 AI 검색 세션을 찾을 수 없습니다."),
    AI_TAG_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "AI400_3", "존재하지 않거나 이미 삭제된 태그 타입입니다."),
    AI_TASK_ID_INVALID(HttpStatus.BAD_REQUEST, "AI400_4", "유효하지 않거나 만료된 작업 ID(taskId)입니다."),
    AI_SESSION_UNAUTHORIZED(HttpStatus.FORBIDDEN, "AI403_1", "해당 세션의 소유자가 아닙니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

