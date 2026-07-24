package com.omo.backend.domain.budget.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BudgetErrorCode implements BaseErrorCode {

    CITY_BUDGET_NOT_CONFIGURED(
            HttpStatus.CONFLICT,
            "BUDGET409_1",
            "도시의 예산 정보가 설정되지 않았습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
