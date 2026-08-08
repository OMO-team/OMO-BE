package com.omo.backend.domain.aisearch.service;

import tools.jackson.databind.json.JsonMapper;
import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;
import com.omo.backend.domain.aisearch.entity.AiSearchLog;
import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import com.omo.backend.domain.aisearch.enums.ConditionType;
import com.omo.backend.domain.aisearch.enums.TaskStatus;
import com.omo.backend.domain.aisearch.exception.AiSearchErrorCode;
import com.omo.backend.domain.aisearch.repository.AiSearchLogRepository;
import com.omo.backend.domain.aisearch.repository.AiSearchSessionRepository;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.report.converter.ReportConverter;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.enums.ResourceTopic;
import com.omo.backend.domain.report.repository.CityRelatedResourceRepository;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiSearchProcessor {

    private final AiSearchLogRepository aiSearchLogRepository;
    private final AiSearchSessionRepository aiSearchSessionRepository;
    private final CityRepository cityRepository;
    private final CityRelatedResourceRepository cityRelatedResourceRepository;
    private final JsonMapper objectMapper;
    private final AiClient aiClient;
    private final RelaxationSuggester relaxationSuggester;

    @Transactional
    public void process(String taskId, Long sessionId, String searchQuery, boolean isRefine) {
        long startTime = System.currentTimeMillis();
        AiSearchSession session = aiSearchSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(AiSearchErrorCode.AI_SESSION_NOT_FOUND));

        // 1. 자연어 -> 파싱
        AiSearchResponseDTO.ParsedConditions currentParsed;
        List<String> validCountryNames = cityRepository.findDistinctCountryNames().stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .distinct()
                .toList();

        try {
            currentParsed = aiClient.callWithSchema(
                    buildParsePrompt(searchQuery, validCountryNames),
                    GeminiSchemas.parsedConditionsSchema(),
                    AiSearchResponseDTO.ParsedConditions.class);
        } catch (Exception e) {
            log.error("[AI 파싱 오류] TaskId: {}", taskId, e);
            throw new GeneralException(AiSearchErrorCode.AI_ANALYSIS_FAILED);

        }

        String normalizedCountry = normalizeCountryName(currentParsed.mentionedCountry(), validCountryNames);

        currentParsed = AiSearchResponseDTO.ParsedConditions.builder()
                .requireHighSafety(currentParsed.requireHighSafety())
                .requireEasyVisa(currentParsed.requireEasyVisa())
                .requireGoodHousing(currentParsed.requireGoodHousing())
                .requireGoodInfra(currentParsed.requireGoodInfra())
                .requireEnglishOnly(currentParsed.requireEnglishOnly())
                .maxBudgetKrw(currentParsed.maxBudgetKrw())
                .mentionedCountry(normalizedCountry)
                .mentionedPurpose(currentParsed.mentionedPurpose())
                .build();

        // 2. 세션에 누적된 이전 조건과 병합
        AiSearchResponseDTO.ParsedConditions previousParsed = parsePreviousConditions(session);
        AiSearchResponseDTO.ParsedConditions parsed = Boolean.TRUE.equals(isRefine)
                ? mergeConditions(previousParsed, currentParsed)
                : currentParsed;

        // 3. DB 필터링
        List<City> candidates = cityRepository.findCandidatesByConditions(
                parsed.maxBudgetKrw(),
                parsed.requireHighSafety(),
                parsed.requireEnglishOnly(),
                parsed.requireEasyVisa(),
                parsed.requireGoodHousing(),
                parsed.requireGoodInfra(),
                parsed.mentionedCountry()
        );

        if (candidates.isEmpty()) {
            saveEmptyResult(taskId, session, isRefine, parsed, searchQuery);
            return;
        }

        // 4. 후보 중 선택 + 서술 AI 호출
        AiSearchResponseDTO.AiRawResult raw;
        try {
            raw = aiClient.callWithSchema(
                    buildSelectPrompt(searchQuery, parsed, candidates),
                    AiSearchResponseDTO.AiRawResult.class
            );
        } catch (Exception e) {
            log.error("[AI 선택 오류] TaskId: {}", taskId, e);
            throw new GeneralException(AiSearchErrorCode.AI_ANALYSIS_FAILED); // 여기도 동일

        }

        // 5. 화이트리스트 검증
        Set<Long> candidateIds = candidates.stream().map(City::getCityId).collect(Collectors.toSet());
        List<Long> validatedIds = raw.recommendedCityIds() == null ? List.of() :
                raw.recommendedCityIds().stream().filter(candidateIds::contains).toList();

        if (validatedIds.isEmpty()) {
            log.warn("[AI 환각 감지] TaskId: {}, raw={}, candidateIds={}",
                    taskId, raw.recommendedCityIds(), candidateIds);
            saveEmptyResult(taskId, session, isRefine, parsed, searchQuery);
            return;
        }

        // 6. 실제 DB 값으로 재조립
        List<CityResponseDTO.CitySummary> recommendedCities = candidates.stream()
                .filter(c -> validatedIds.contains(c.getCityId()))
                .map(CityResponseDTO.CitySummary::from)
                .toList();

        List<ReportResponseDTO.ResourceDTO> resources = buildResources(validatedIds, raw.primaryConditionType());

        int elapsedSeconds = (int) ((System.currentTimeMillis() - startTime) / 1000);

        AiSearchResponseDTO.BriefingData briefingData = AiSearchResponseDTO.BriefingData.of(
                elapsedSeconds, raw.summary(), raw.extractedTags(), recommendedCities, resources
        );

        AiSearchResponseDTO.BriefingStatusResult finalResult = AiSearchResponseDTO.BriefingStatusResult.of(
                TaskStatus.COMPLETED, isRefine, parsed.mentionedPurpose(), parsed.mentionedCountry(),
                briefingData, null, null
        );

        saveResultAndComplete(taskId, session, finalResult, parsed);

    }

    private String buildParsePrompt(String searchQuery, List<String> validCountryNames) {
        return """
        너는 해외 이주/어학연수 도시 추천 서비스의 조건 분석기다.
        아래 사용자 문장을 분석해서 조건을 구조화된 값으로만 추출해라.

        규칙:
        1. 문장에 명시적으로 언급되거나 강하게 암시된 조건만 값을 채워라.
        2. 언급이 없는 항목은 반드시 null로 남겨라. 임의로 추측해서 채우지 마라.
        3. 불리언 항목
              - "치안 좋은", "안전한" → requireHighSafety=true
              - "비자 쉬운", "무비자" → requireEasyVisa=true
              - "영어 소통", "영어권" → requireEnglishOnly=true
              - "주거 우수", "집 구하기 쉬운" → requireGoodHousing=true
              - "인프라 우수", "편의시설" → requireGoodInfra=true
              - 조건이 필요하지 않거나 상관 없다는 의미가 명시되면 false를 채운다.
              - 조건에 대한 언급 자체가 없으면 null이다.
        4. maxBudgetKrw는 월 예산 기준 만원 단위 정수로 변환한다.
             예) "2000000" → 200
        5. mentionedCountry는 문장에 국가명이 언급된 경우에만 채우되, 반드시 아래 국가 목록 중 정확히 일치하는 표기로 채워라.
           국가 목록: %s
              - 한국어로 언급되어도(예: "몰타") 반드시 목록에 있는 정확한 표기로 변환해서 채워라.
              - 목록에 없는 국가가 언급되면 null로 남겨라.
        6. mentionedPurpose는 반드시 다음 중 하나여야 한다: WORKING_HOLIDAY, EXCHANGE_STUDENT, INTERNSHIP.
             - "워킹홀리데이", "워홀" → WORKING_HOLIDAY
             - "교환학생" → EXCHANGE_STUDENT
             - "인턴", "인턴십" → INTERNSHIP
             - 위 세 가지 중 어디에도 명확히 해당하지 않으면 null로 남겨라.
        7. 국가명, 예산, 조건을 임의로 만들어 넣지 마라.
        8. 반드시 ParsedConditions JSON만 출력한다. 설명, 코드블록, 마크다운은 출력하지 않는다.
        9. JSON 외의 설명, 코드블록(```), 마크다운, 자연어 문장을 출력하지 마라.

        사용자 문장: "%s"
        """.formatted(String.join(", ", validCountryNames), searchQuery);
    }

    private String buildSelectPrompt(String searchQuery, AiSearchResponseDTO.ParsedConditions parsed, List<City> candidates) {
        String candidateJson = candidates.stream()
                .map(c -> """
        {"cityId": %d, "cityName": "%s", "countryName": "%s", "safetyScore": %s, "monthlyCost": %d, "visaScore": %s, "housingScore": %s, "internetScore": %s, "languageScore": %s}\
        """.formatted(
                        c.getCityId(), c.getName(), c.getCountry().getName(),
                        c.getSafetyScore(), c.getMonthlyCost(), c.getVisaScore(),
                        c.getHousingScore(), c.getInternetScore(), c.getLanguageScore()
                ))
                .collect(Collectors.joining(",\n"));

        return """
        너는 해외 이주/어학연수 도시 추천 서비스의 브리핑 작성자다.
        아래는 사용자 질문과, 조건 필터링을 이미 통과한 후보 도시 목록(실제 DB 데이터)이다.

        절대 규칙:
        - recommendedCityIds에는 아래 후보 목록에 있는 cityId만 넣어라. 목록에 없는 ID를 지어내면 안 된다.
        - summary(서술) 작성 시 각 도시를 언급할 때 가능하면 '도시명(국가명)' 또는 '국가명의 도시명' 형태로 국가명을 함께 밝혀서 작성하라. (예: "덴마크의 오르후스", "루마니아의 클루지나포카")
        - summary(서술)에는 점수나 수치를 괄호로 나열하지 말고, "가장 안전한", "예산 조건에 가장 잘 맞는", "영어 활용도가 특히 높은"처럼 정성적인 표현으로 서술하라. 구체적 숫자(4.8점, 250만원 등)는 summary 문장에 직접 쓰지 마라. 숫자 데이터는 별도 필드(recommendedCities)로 이미 전달되니 summary는 자연스러운 설명에 집중한다.
        - summary(서술)에는 후보 데이터에 존재하는 정보와 사용자 질문만 근거로 작성한다. 후보 도시의 비교 및 추천 근거는 반드시 후보 데이터에 포함된 정보만 사용한다.
        - 후보 데이터에 없는 특징(기후, 문화, 치안 수준, 생활비, 한국인 비율 등)을 추론하거나 추가하지 않는다. 
        - 조건을 만족하는 후보가 3개 이상이면 상위 3개만 추천한다.
        - 조건을 만족하는 후보가 1~2개이면 해당 도시만 추천한다. 
        - 왜 골랐는지 자연스러운 한국어 문장으로 요약해라.
        - extractedTags는 오직 필터링 조건(치안/예산/영어/비자/주거/인프라)에 관한 태그만 담아라.
                      - "워킹홀리데이", "교환학생", "인턴십" 같은 목적(purpose) 관련 단어는 태그에 절대 포함하지 마라. 그건 activePurpose 필드에만 담긴다.
                      - "안전한 도시", "추천 도시" 같은 조건과 무관한 일반적 수식어도 태그로 넣지 마라.
                      - 좋은 예: "치안 우수 (4점 이상)", "영어 소통 가능", "예산 250만원 이하", "인프라 우수"
                      - 나쁜 예: "교환학생", "안전한 도시", "추천 도시"    
        - primaryConditionType은 사용자가 가장 중요하게 여긴 조건 하나를 SAFETY/BUDGET/LANGUAGE/VISA/HOUSING/INFRA 중에서 골라라.
        - 후보 중 필수 조건을 만족하는 도시가 하나도 없으면 isEmptyResult를 true로, recommendedCityIds는 빈 배열로 해라.
        - 반드시 RecommendationResponse JSON만 출력한다. 설명, 코드블록, 마크다운 금지.
        - activePurpose는 추출된 조건(mentionedPurpose)에 이미 값이 있으면 그 값을 그대로 사용하고, 없으면 null로 둔다. 임의로 추측하지 마라.
        
        사용자 질문: "%s"
        추출된 조건: %s
        후보 도시 목록:
        [%s]
        """.formatted(searchQuery, parsed.toString(), candidateJson);
    }

    private Optional<ResourceTopic> mapToResourceTopic(ConditionType conditionType) {
        return switch (conditionType) {
            case SAFETY -> Optional.of(ResourceTopic.SAFETY);
            case VISA -> Optional.of(ResourceTopic.VISA);
            case HOUSING -> Optional.of(ResourceTopic.HOUSING);
            case BUDGET -> Optional.of(ResourceTopic.COST);
            case LANGUAGE, INFRA -> Optional.empty();
        };
    }

    private AiSearchResponseDTO.ParsedConditions mergeConditions(
            AiSearchResponseDTO.ParsedConditions previous, AiSearchResponseDTO.ParsedConditions current) {
        if (previous == null) return current;
        return AiSearchResponseDTO.ParsedConditions.builder()
                .requireHighSafety(current.requireHighSafety() != null ? current.requireHighSafety() : previous.requireHighSafety())
                .requireEasyVisa(current.requireEasyVisa() != null ? current.requireEasyVisa() : previous.requireEasyVisa())
                .requireGoodHousing(current.requireGoodHousing() != null ? current.requireGoodHousing() : previous.requireGoodHousing())
                .requireGoodInfra(current.requireGoodInfra() != null ? current.requireGoodInfra() : previous.requireGoodInfra())
                .requireEnglishOnly(current.requireEnglishOnly() != null ? current.requireEnglishOnly() : previous.requireEnglishOnly())
                .maxBudgetKrw(current.maxBudgetKrw() != null ? current.maxBudgetKrw() : previous.maxBudgetKrw())
                .mentionedCountry(current.mentionedCountry() != null ? current.mentionedCountry() : previous.mentionedCountry())
                .mentionedPurpose(current.mentionedPurpose() != null ? current.mentionedPurpose() : previous.mentionedPurpose())
                .build();
    }

    // ── 참고자료 조회
    private List<ReportResponseDTO.ResourceDTO> buildResources(List<Long> cityIds, ConditionType conditionType) {
        Optional<ResourceTopic> topic = mapToResourceTopic(conditionType);

        if (topic.isPresent()) {
            return cityIds.stream()
                    .flatMap(cityId -> cityRelatedResourceRepository
                            .findByCityIdAndTopicAndDeletedAtIsNull(cityId, topic.get())
                            .stream())
                            .limit(2)
                    .map(ReportConverter::toResourceDTO)
                    .toList();
        }

        // 매핑 안 되는 조건(LANGUAGE, INFRA)이면 도시별 전체 참고자료 중 상위 2개로 폴백
        return cityIds.stream()
                .flatMap(cityId -> cityRelatedResourceRepository
                        .findByCityIdAndDeletedAtIsNull(cityId)
                        .stream())
                        .limit(2)
                .map(ReportConverter::toResourceDTO)
                .toList();
    }

    // ── 빈 결과 처리: 조건 완화 제안은 backend 룰 기반 ──
    private void saveEmptyResult(String taskId, AiSearchSession session, Boolean isRefine,
                                 AiSearchResponseDTO.ParsedConditions parsed, String searchQuery) {
        List<AiSearchResponseDTO.SuggestedRelaxation> relaxations = relaxationSuggester.suggest(parsed, searchQuery);

        AiSearchResponseDTO.BriefingStatusResult result = AiSearchResponseDTO.BriefingStatusResult.of(
                TaskStatus.COMPLETED, isRefine, parsed.mentionedPurpose(), parsed.mentionedCountry(),
                null, "선택하신 조건에 맞는 도시가 존재하지 않습니다.", relaxations
        );
        saveResultAndComplete(taskId, session, result, parsed);
    }

    private AiSearchResponseDTO.ParsedConditions parsePreviousConditions(AiSearchSession session) {
        try {
            return objectMapper.readValue(session.getAccumulatedConditions(), AiSearchResponseDTO.ParsedConditions.class);
        } catch (Exception e) {
            return null;
        }
    }

    private void saveResultAndComplete(String taskId, AiSearchSession session,
                                       AiSearchResponseDTO.BriefingStatusResult result,
                                       AiSearchResponseDTO.ParsedConditions parsed) {
        String json;
        try {
            json = objectMapper.writeValueAsString(result);
            session.updateAccumulatedConditions(objectMapper.writeValueAsString(parsed));
        } catch (Exception e) {
            log.error("[JSON 직렬화 오류] TaskId: {}, error: {}", taskId, e.getMessage(), e);
            throw new GeneralException(AiSearchErrorCode.AI_ANALYSIS_FAILED);
        }

        AiSearchLog searchLog = aiSearchLogRepository.findByTaskId(taskId)
                .orElseThrow(() -> new GeneralException(AiSearchErrorCode.AI_TASK_ID_INVALID));
        searchLog.updateAiResponse(json, result.emptyResultMessage() != null);
        aiSearchLogRepository.saveAndFlush(searchLog);

    }

    private String normalizeCountryName(String parsedCountry, List<String> validCountryNames) {
        if (parsedCountry == null || parsedCountry.isBlank()) {
            return null;
        }

        String trimmedParsed = parsedCountry.trim();

        return validCountryNames.stream()
                .filter(dbCountry -> dbCountry.equalsIgnoreCase(trimmedParsed))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("[국가명 매칭 실패] Gemini 반환 국가: '{}' 가 DB 목록에 존재하지 않아 null 처리함", parsedCountry);
                    return null; // 일치하는 국가가 없으면 무효 처리
                });
    }

}
