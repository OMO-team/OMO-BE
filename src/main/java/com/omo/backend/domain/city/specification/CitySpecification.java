package com.omo.backend.domain.city.specification;

import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.city.enums.CityDifficulty;
import com.omo.backend.domain.city.enums.CityStayDuration;
import com.omo.backend.domain.purpose.enums.PurposeEnum;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;


public class CitySpecification {

    // 목적 필터
    public static Specification<City> hasPurpose(String purpose){
        if (purpose == null || purpose.isBlank()) return null;
        PurposeEnum purposeEnum = PurposeEnum.from(purpose);
        return (root, query, cb) -> {
            return cb.equal(
                    root.join("cityPurposes")
                            .join("purpose")
                            .get("type"),
                    purposeEnum
            );
        };
    }

    // 키워드 검색 (도시명, 국가명, 설명)
    public static Specification<City> hasKeyword(String keyword){
        if (keyword == null || keyword.isBlank()) return null;
        // SQL LIKE 특수문자 이스케이프 (순서 중요: \ 먼저)
        String escaped = keyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        String like = "%" + escaped + "%";
        return (root, query, cb) -> cb.or(
                cb.like(root.get("name"), like, '\\'),
                cb.like(root.get("description"), like, '\\'),
                cb.like(root.join("country", JoinType.LEFT).get("name"), like, '\\')
        );
    }

    //국가 코드 필터
    public static Specification<City> hasCountry(String countryCode){
        if (countryCode == null || countryCode.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(root.get("country").get("code"), countryCode);
    }

    // 최대 생활비
    public static Specification<City> hasMaxCost(Integer maxCost){
        if (maxCost == null) return null;
        return (root, query, cb) -> {
            return cb.lessThanOrEqualTo(root.get("monthlyCost"), maxCost);
        };
    }

    //최소 치안 점수
    public static Specification<City> hasMinSafety(BigDecimal minSafety){
        if (minSafety == null) return null;
        return (root, query, cb) -> {
            return cb.greaterThanOrEqualTo(root.get("safetyScore"), minSafety);
        };
    }


    //숙소 난이도 -> 선택한 난이도값 이상의 도시들 반환
    public static Specification<City> hasHousingDifficulty(String difficulty){
        if (difficulty == null || difficulty.isBlank()) return null;
        BigDecimal threshold = CityDifficulty.from(difficulty).getMinScore();
        return ((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("housingScore"), threshold));
    }

    //비자 난이도 -> 선택한 난이도값 이상의 도시들 반환
    public static Specification<City> hasVisaDifficulty(String difficulty){
        if (difficulty == null || difficulty.isBlank()) return null;
        BigDecimal threshold = CityDifficulty.from(difficulty).getMinScore();
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("visaScore"), threshold);
    }

    public static Specification<City> hasStayDuration(String stayDuration){
        if (stayDuration == null || stayDuration.isBlank()) return null;
        CityStayDuration duration = CityStayDuration.from(stayDuration);
        return (root, query, cb) ->
                cb.equal(root.get("stayDuration"), duration);
    }

    // 대륙 필터
    public static Specification<City> hasContinent(String continent) {
        if (continent == null || continent.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(root.join("country", JoinType.LEFT).get("continent"), continent);
    }

    public static Specification<City> isNotDeleted() {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.isNull(root.get("deletedAt"));  // ← 여기 return 있어야 함
        };
    }
}
