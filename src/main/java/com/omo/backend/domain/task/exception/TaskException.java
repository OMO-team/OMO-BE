package com.omo.backend.domain.task.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class TaskException extends GeneralException {

    public TaskException(BaseErrorCode code) {
        super(code);
    }
}
