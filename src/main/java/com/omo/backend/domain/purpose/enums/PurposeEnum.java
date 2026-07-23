package com.omo.backend.domain.purpose.enums;

import com.omo.backend.domain.purpose.exception.PurposeErrorCode;
import com.omo.backend.domain.purpose.exception.PurposeException;

import java.util.Locale;

public enum PurposeEnum {
    WORKING_HOLIDAY,
    EXCHANGE_STUDENT,
    INTERNSHIP
;
    public static PurposeEnum from(String value){
        try{
            return valueOf(value.toUpperCase(Locale.ROOT));
        }catch (IllegalArgumentException e){
            throw new PurposeException(PurposeErrorCode.INVALID_PURPOSE_TYPE);
        }
    }
    }
