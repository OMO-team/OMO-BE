package com.omo.backend.domain.aisearch.scheduler;

import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import com.omo.backend.domain.aisearch.repository.AiSearchSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiSessionCleanupScheduler {

    private final AiSearchSessionRepository aiSearchSessionRepository;

    /**
     * 매일 새벽 3시에 30일이 지난 오래된 세션을 자동 Soft Delete
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<AiSearchSession> oldSessions = aiSearchSessionRepository
                .findAllByCreatedAtBeforeAndDeletedAtIsNull(threshold);

        if (oldSessions.isEmpty()) {
            log.info("[Scheduler] 정리할 오래된 AI 세션이 없습니다.");
            return;
        }

        for (AiSearchSession session : oldSessions) {
            session.delete();
        }

        log.info("[Scheduler] 총 {}개의 오래된 AI 세션이 자동 삭제 되었습니다.", oldSessions.size());
    }
}