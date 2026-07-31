package com.omo.backend.domain.inquiry.exception;

import com.omo.backend.global.apiPayload.exception.GeneralException;

public class InquiryException extends GeneralException {

    public InquiryException(InquiryErrorCode code) {
        super(code);
    }
}
