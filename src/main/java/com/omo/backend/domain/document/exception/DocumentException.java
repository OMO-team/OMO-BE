package com.omo.backend.domain.document.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class DocumentException extends GeneralException {

    public DocumentException(BaseErrorCode code) {
        super(code);
    }
}
