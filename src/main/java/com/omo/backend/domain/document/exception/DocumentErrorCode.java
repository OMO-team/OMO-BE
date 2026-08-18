package com.omo.backend.domain.document.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DocumentErrorCode implements BaseErrorCode {

    TASK_DOCUMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "DOCUMENT404_1",
            "존재하지 않는 작업 서류입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
