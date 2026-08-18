package com.omo.backend.domain.purpose.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class PurposeException extends GeneralException {
    public PurposeException(BaseErrorCode code) {super(code);}
}
