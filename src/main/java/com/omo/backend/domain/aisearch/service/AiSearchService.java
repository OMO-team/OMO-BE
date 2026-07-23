package com.omo.backend.domain.aisearch.service;

import com.omo.backend.domain.aisearch.converter.AiSearchConverter;
import com.omo.backend.domain.aisearch.dto.AiSearchRequestDTO;
import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;
import com.omo.backend.domain.aisearch.dto.RecommendPromptChipResponseDTO;
import com.omo.backend.domain.aisearch.entity.AiSearchLog;
import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;
import com.omo.backend.domain.aisearch.enums.TaskStatus;
import com.omo.backend.domain.aisearch.exception.AiSearchErrorCode;
import com.omo.backend.domain.aisearch.repository.AiSearchLogRepository;
import com.omo.backend.domain.aisearch.repository.AiSearchSessionRepository;
import com.omo.backend.domain.aisearch.repository.RecommendPromptChipRepository;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiSearchService {

    private final RecommendPromptChipRepository recommendPromptChipRepository;
    private final AiSearchSessionRepository aiSearchSessionRepository;
    private final AiSearchLogRepository aiSearchLogRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Redis Key Prefix 및 만료시간(TTL)
    private static final String TASK_PREFIX = "ai_task:";
    private static final long TASK_TTL_MINUTES = 30; // 30분 후 자동 삭제

    /**
     * 현재 활성화 상태(isActive = true)인 추천 프롬프트 칩 목록을 조회
     **/
    public RecommendPromptChipResponseDTO.ChipList getActiveRecommendPromptChips() {
        List<RecommendPromptChip> chips = recommendPromptChipRepository.findAllByIsActiveTrue();

        return AiSearchConverter.toChipList(chips);
    }

    /**
     * AI 스마트 브리핑 분석 요청 (요청 ~ 로딩 화면까지)
     */
    @Transactional
    public AiSearchResponseDTO.BriefingInitResult requestSmartBriefing(AiSearchRequestDTO.BriefingRequest request) {

        // 1. 세션 조회 or 생성
        AiSearchSession session = getOrCreateSession(request.sessionId(), request.isRefine());

        // 2. 고유 taskId 생성
        String taskId = "task_" + UUID.randomUUID().toString().replace("-", "").substring(0,12);
        log.info("[AI Search Request] SessionId: {}, TaskId: {}, Query: {}, isRefine: {}",
                session.getId(), taskId, request.searchQuery(), request.isRefine());

        // 3. AI 검색 로그 저장
        AiSearchLog searchLog = AiSearchLog.createSearchLog(session, request.searchQuery(), request.isRefine(), taskId);
        aiSearchLogRepository.save(searchLog);

        // 4. Redis에 taskId 초기 작업 상태 저장
        saveInitialTaskStatus(taskId);


        // 5. 응답 DTO 반환
        return AiSearchConverter.toBriefingInitResult(session.getId(), taskId);
    }

    private AiSearchSession getOrCreateSession(Long sessionId, Boolean isRefine) {
        // 이어묻기(isRefine = true)이고, sessionId가 제공된 경우 -> 기존 세션 조회
        if (Boolean.TRUE.equals(isRefine) && sessionId != null) {
            return aiSearchSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new GeneralException(AiSearchErrorCode.AI_SESSION_NOT_FOUND));
        }

        // 첫 검색이거나 세션 Id가 없는 경우 -> 새 세션 생성
        AiSearchSession newSession = AiSearchSession.createSession();
        return aiSearchSessionRepository.save(newSession);
    }

    private void saveInitialTaskStatus(String taskId) {
        String key = TASK_PREFIX + taskId;
        redisTemplate.opsForValue().set(key, "PROCESSING", Duration.ofMinutes(TASK_TTL_MINUTES));
    }

    /**
     * AI 스마트 브리핑 상태 및 결과 조회 (결과 화면까지)
     */
    public AiSearchResponseDTO.BriefingStatusResult getSmartBriefingStatus(String taskId) {
        String key = TASK_PREFIX + taskId;
        String statusStr = redisTemplate.opsForValue().get(key);

        // 1. Redis에 작업 키가 없는 경우 (만료 or 유효하지 않은 taskId)
        if (statusStr == null) {
            throw new GeneralException(AiSearchErrorCode.AI_TASK_ID_INVALID);
        }

        // 2. 진행 중인 경우
        if ("PROCESSING".equals(statusStr)) {
            return AiSearchConverter.toProcessingStatusResult();
        }

        // 3. 작업이 실패한 경우
        if ("FAILED".equals(statusStr)) {
            throw new GeneralException(AiSearchErrorCode.AI_ANALYSIS_FAILED);
        }

        // 4. 완료된 경우
        AiSearchLog searchLog = aiSearchLogRepository.findByTaskId(taskId)
                .orElseThrow(() -> new GeneralException(AiSearchErrorCode.AI_TASK_ID_INVALID));

        try {
            AiSearchResponseDTO.BriefingStatusResult result = objectMapper.readValue(
                    searchLog.getAiResponse(),
                    AiSearchResponseDTO.BriefingStatusResult.class
            );

            return AiSearchResponseDTO.BriefingStatusResult.builder()
                    .status(TaskStatus.COMPLETED)
                    .isRefine(searchLog.getIsRefine())
                    .activePurpose(result.activePurpose())
                    .selectedCountry(result.selectedCountry())
                    .briefingData(result.briefingData())
                    .emptyResultMessage(result.emptyResultMessage())
                    .suggestedRelaxations(result.suggestedRelaxations())
                    .build();

        } catch (Exception e) {
            log.error("[JSON Parsing Error] TaskId: {}, Error: {}", taskId, e.getMessage());
            throw new GeneralException(AiSearchErrorCode.AI_ANALYSIS_FAILED);
        }
    }
}
