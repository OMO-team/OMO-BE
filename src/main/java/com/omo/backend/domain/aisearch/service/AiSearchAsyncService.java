package com.omo.backend.domain.aisearch.service;
import com.omo.backend.domain.aisearch.enums.TaskStatus;
import com.omo.backend.domain.aisearch.event.AiBriefingRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSearchAsyncService {
    private final AiSearchProcessor aiSearchProcessor;
    private final StringRedisTemplate redisTemplate;

    private static final String TASK_PREFIX = "ai_task:";
    private static final long TASK_TTL_MINUTES = 30;

    /**
     * 백그라운드에서 진짜 AI를 호출하고 DB/Redis에 결과를 저장하는 비동기 메서드
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBriefingRequest(AiBriefingRequestedEvent event) {
        String taskId = event.taskId();
        try  {
            aiSearchProcessor.process(taskId, event.sessionId(), event.searchQuery(), event.isRefine());
        } catch (Exception e) {
            log.error("[AI Async 오류] TaskId: {}", taskId, e);
            redisTemplate.opsForValue().set(TASK_PREFIX + taskId, TaskStatus.FAILED.name(), Duration.ofMinutes(TASK_TTL_MINUTES));
            return;
        }
        redisTemplate.opsForValue().set(TASK_PREFIX + taskId, TaskStatus.COMPLETED.name(), Duration.ofMinutes(TASK_TTL_MINUTES));

    }
}
