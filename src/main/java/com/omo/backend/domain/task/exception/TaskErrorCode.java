package com.omo.backend.domain.task.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TaskErrorCode implements BaseErrorCode {

    TASK_LOCKED(
            HttpStatus.CONFLICT,
            "TASK409_1",
            "선행 태스크가 완료되지 않아 잠긴 태스크입니다."
    ),
    TASK_HAS_DOCUMENTS(
            HttpStatus.CONFLICT,
            "TASK409_2",
            "서류가 있는 태스크는 직접 완료할 수 없습니다."
    ),
    TASK_ALREADY_COMPLETED(
            HttpStatus.CONFLICT,
            "TASK409_3",
            "이미 완료된 태스크입니다."
    ),
    TASK_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TASK404_1",
            "존재하지 않는 태스크입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
