package com.omo.backend.domain.aisearch.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class AiSearchException extends GeneralException {
    public AiSearchException(BaseErrorCode code) {
        super(code);
    }
}