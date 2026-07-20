package com.omo.backend.domain.aisearch.service;

import com.omo.backend.domain.aisearch.converter.AiSearchConverter;
import com.omo.backend.domain.aisearch.dto.AiSearchRequestDTO;
import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;
import com.omo.backend.domain.aisearch.dto.RecommendPromptChipResponseDTO;
import com.omo.backend.domain.aisearch.entity.AiSearchLog;
import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;
import com.omo.backend.domain.aisearch.exception.AiSearchErrorCode;
import com.omo.backend.domain.aisearch.repository.AiSearchLogRepository;
import com.omo.backend.domain.aisearch.repository.AiSearchSessionRepository;
import com.omo.backend.domain.aisearch.repository.RecommendPromptChipRepository;
import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.code.GeneralErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 2. AI 검색 로그 저장
        AiSearchLog searchLog = AiSearchLog.createSearchLog(session, request.searchQuery(), request.isRefine());
        aiSearchLogRepository.save(searchLog);

        // 3. 고유 taskId 생성
        String taskId = "task_" + UUID.randomUUID().toString().replace("-", "").substring(0,12);
        log.info("[AI Search Request] SessionId: {}, TaskId: {}, Query: {}, isRefine: {}",
                session.getId(), taskId, request.searchQuery(), request.isRefine());

        // TODO: 레디스에 taskId 임시 저장하기

        // 4. 응답 DTO 반환
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
}
