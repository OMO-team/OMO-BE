package com.omo.backend.domain.country.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class CountryException extends GeneralException {
    public CountryException(BaseErrorCode code) {super(code);}
}
