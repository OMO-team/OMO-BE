package com.omo.backend.domain.city.dto;

import java.math.BigDecimal;

public class CityRequestDTO {

    // 필터 조회
    public record CityFilterRequest(
            String keyword,      // 상단 검색바 전용 (AI 검색 시 null)
            String purposeType,       // 목적 탭 (WORKING_HOLIDAY, EXCHANGE_STUDENT, INTERNSHIP, null=전체)
            String countryCode,       // 지역 필터 (국가 코드, null=전체)
            Integer maxMonthlyCost,   // 최대 월 생활비 (만원 단위, null=상관없음)
            BigDecimal minSafetyScore,   // 최소 치안 점수 (5.0/4.0/3.0, null=상관없음)
            String housingDifficulty,    // 숙소 난이도 (EASY/NORMAL/HARD, null=상관없음)
            String visaDifficulty        // 비자 난이도 (EASY/NORMAL/HARD, null=상관없음)
    ) {}


}
