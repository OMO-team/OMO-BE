package com.omo.backend.domain.aisearch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConditionType {
    SAFETY("치안"),
    BUDGET("예산"),
    LANGUAGE("언어"),
    VISA("비자"),
    HOUSING("주거"),
    INFRA("인프라");

    private final String description;
}
