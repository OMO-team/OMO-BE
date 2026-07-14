package com.omo.backend.domain.report.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class ReportException extends GeneralException {
    public ReportException(BaseErrorCode code) {
        super(code);
    }
}
