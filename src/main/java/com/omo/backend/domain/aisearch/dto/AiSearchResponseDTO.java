package com.omo.backend.domain.aisearch.dto;

import com.omo.backend.domain.aisearch.enums.ConditionType;
import com.omo.backend.domain.aisearch.enums.TaskStatus;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class AiSearchResponseDTO {

    // 1번 API 응답 (요청 접수 ~ taskId 발급 ~ 로딩 화면)
    @Builder
    public record BriefingInitResult(
            @Schema(description = "대화 세션 ID", example = "12345")
            Long sessionId,

            @Schema(description = "상태 조회용 작업 ID", example = "task_req_9988")
            String taskId
    ) {}

    // 2번 API 응답 (AI 브리핑 상태 및 결과 조회)
    @Builder
    public record BriefingStatusResult(
            @Schema(description = "작업 상태 (PROCESSING / COMPLETED / FAILED)", example = "COMPLETED")
            TaskStatus status,

            @Schema(description = "이어묻기 결과 여부", example = "false")
            Boolean isRefine,

            @Schema(description = "활성화된 목적", example = "어학연수")
            String activePurpose,

            @Schema(description = "선택된 국가", example = "Malta")
            String selectedCountry,

            @Schema(description = "AI 스마트 브리핑 상세 데이터(PROCESSING 시 null)")
            BriefingData briefingData,

            @Schema(description = "결과 0건일 때 안내 메세지 (정상 결과 존재 시 null)", example = "선택하신 조건에 맞는 도시가 존재하지 않습니다.")
            String emptyResultMessage,

            @Schema(description = "결과 0건일 때 조건 완화 제안 목록 (정상 결과 존재 시 null)")
            List<SuggestedRelaxation> suggestedRelaxations
    ){ public static BriefingStatusResult of(
            TaskStatus status,
            Boolean isRefine,
            String activePurpose,
            String selectedCountry,
            BriefingData briefingData,
            String emptyResultMessage,
            List<SuggestedRelaxation> suggestedRelaxations){

        return BriefingStatusResult.builder()
                .status(status)
                .isRefine(isRefine)
                .activePurpose(activePurpose)
                .selectedCountry(selectedCountry)
                .briefingData(briefingData)
                .emptyResultMessage(emptyResultMessage)
                .suggestedRelaxations(suggestedRelaxations)
                .build();
            }
    }

    @Builder
    public record BriefingData(
            @Schema(description = "AI 분석 소요 시간 (초)", example = "21")
            Integer thinkingTime,

            @Schema(description = "AI 요약 브리핑 본문", example = "영어만으로 생활이 가능하면서 치안이 우수한 도시로는 호주의 '시드니'와 독일의 '뮌헨'을 가장 추천합니다.")
            String summary,

            @Schema(description = "AI가 추출한 조건 태그 리스트", example = "[\"치안 우수 (4점 이상)\", \"영어 소통 가능\", \"예산 250만원 이하\"]")
            List<String> extractedTags,

            @Schema(description = "추천 도시 목록")
            List<CityResponseDTO.CitySummary> recommendedCities,

            @Schema(description = "참고자료 목록")
            List<ReportResponseDTO.ResourceDTO> resources
    ){
        public static BriefingData of(
                Integer thinkingTime,
                String summary,
                List<String> extractedTags,
                List<CityResponseDTO.CitySummary> recommendedCities,
                List<ReportResponseDTO.ResourceDTO> resources
        ) {
            return BriefingData.builder()
                    .thinkingTime(thinkingTime)
                    .summary(summary)
                    .extractedTags(extractedTags)
                    .recommendedCities(recommendedCities)
                    .resources(resources)
                    .build();
        }
    }


    @Builder
    public record SuggestedRelaxation(
            @Schema(description = "완화 유형 (BUDGET, REGION 등)", example = "BUDGET")
            String type,

            @Schema(description = "제안 문구", example = "예산 조건을 20만 원만 높여보세요.")
            String message,

            @Schema(description = "재검색용 쿼리 문장", example = "치안이 좋고 영어로 생활 가능한 220만원 이하 도시")
            String query
    ) {
        public static SuggestedRelaxation of(String type, String message, String query) {
            return SuggestedRelaxation.builder()
                    .type(type)
                    .message(message)
                    .query(query)
                    .build();
        }
    }

    // AI가 자연어 분석해 필터 조건으로 추출한 파싱 DTO
    @Builder
    public record ParsedConditions(
            @Schema(description = "치안 우수 조건 선호 여부 (true/false, 언급 없으면 null)", example = "true")
            Boolean requireHighSafety,

            @Schema(description = "비자 용이성 선호 여부 (true/false, 언급 없으면 null)", example = "true")
            Boolean requireEasyVisa,

            @Schema(description = "주거 환경 우수 선호 여부 (true/false, 언급 없으면 null)", example = "false")
            Boolean requireGoodHousing,

            @Schema(description = "인프라 우수 선호 여부 (true/false, 언급 없으면 null)", example = "true")
            Boolean requireGoodInfra,

            @Schema(description = "영어 소통 필수 여부 (true/false, 언급 없으면 null)", example = "true")
            Boolean requireEnglishOnly,

            @Schema(description = "최대 월 예산 (원 단위 정수, 언급 없으면 null)", example = "2000000")
            Integer maxBudgetKrw,

            @Schema(description = "언급된 국가명 (없으면 null)", example = "Malta")
            String mentionedCountry,

            @Schema(description = "언급된 목적 (없으면 null)", example = "어학연수")
            String mentionedPurpose
    ) {}

    // AI가 후보 도시들 중 최종 선택하고 요약문 작성을 내뱉은 AI 원본 응답 DTO
    @Builder
    public record AiRawResult(
            @Schema(description = "AI 분석 소요 시간 (초)", example = "21")
            Integer thinkingTime,

            @Schema(description = "AI 서술 요약 문장", example = "치안과 예산 조건을 만족하는 슬리에마를 추천합니다.")
            String summary,

            @Schema(description = "추출된 조건 태그 목록", example = "치안 우수, 200만원 이하")
            List<String> extractedTags,

            @Schema(description = "추천 도시 ID 목록", example = "[1, 3]")
            List<Long> recommendedCityIds,

            @Schema(description = "주요 강조 조건 유형 (SAFETY, BUDGET, LANGUAGE, VISA, HOUSING, INFRA), example = SAFETY")
            ConditionType primaryConditionType,

            @Schema(description = "활성화된 목적", example = "어학연수")
            String activePurpose,

            @Schema(description = "선택된 국가", example = "Malta")
            String selectedCountry,

            @Schema(description = "결과 0건 여부", example = "false")
            Boolean isEmptyResult
    ) {}
}
