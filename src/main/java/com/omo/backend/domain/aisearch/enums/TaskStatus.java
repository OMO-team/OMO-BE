package com.omo.backend.domain.aisearch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    PROCESSING("분석 진행 중"),
    COMPLETED("분석 완료"),
    FAILED("분석 실패");

    private final String description;
}
