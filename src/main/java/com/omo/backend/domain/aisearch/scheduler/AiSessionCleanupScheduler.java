package com.omo.backend.domain.aisearch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiSessionCleanupScheduler {

    private final SessionCleanupProcessor cleanupProcessor;

    private static final int BATCH_SIZE = 500; // 1회 트랜잭션당 처리할 건수
    private static final int MAX_TOTAL_LIMIT = 10000; // 스케줄러 1회 실행 당 최대 처리 제한

    /**
     * 매일 새벽 3시에 30일이 지난 오래된 세션을 자동 Soft Delete
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        int totalDeletedCount = 0;

        log.info("[Scheduler] 오래된 AI 세션 정제 시작 (Batch Size: {})", BATCH_SIZE);

        while (totalDeletedCount < MAX_TOTAL_LIMIT) {
            int processedInChunk = cleanupProcessor.processBatchChunk(threshold, BATCH_SIZE);

            if (processedInChunk == 0) {
                break;
            }

            totalDeletedCount += processedInChunk;
            log.debug("[Scheduler] 삭제 진행 중 - 현재까지 {}건 삭제 완료", totalDeletedCount);
        }

        if (totalDeletedCount == 0) {
            log.info("[Scheduler] 정리할 오래된 AI 세션이 없습니다.");
        } else {
            log.info("[Scheduler] 총 {}개의 오래된 AI 세션이 성공적으로 삭제되었습니다.", totalDeletedCount);
        }
    }
}