package com.omo.backend.domain.budget.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class BudgetException extends GeneralException {

    public BudgetException(BaseErrorCode code) {
        super(code);
    }
}
