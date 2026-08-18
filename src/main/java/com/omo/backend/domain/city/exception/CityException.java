package com.omo.backend.domain.city.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class CityException extends GeneralException {
    public CityException(BaseErrorCode code) {super(code);}
}
