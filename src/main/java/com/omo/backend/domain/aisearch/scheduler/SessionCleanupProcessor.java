package com.omo.backend.domain.aisearch.scheduler;


import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import com.omo.backend.domain.aisearch.repository.AiSearchSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SessionCleanupProcessor {

    private final AiSearchSessionRepository aiSearchSessionRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int processBatchChunk(LocalDateTime threshold, int batchSize) {
        Slice<AiSearchSession> chunk = aiSearchSessionRepository
                .findAllByCreatedAtBeforeAndDeletedAtIsNull(threshold, PageRequest.of(0, batchSize));

        if (chunk.isEmpty()) {
            return 0;
        }

        for (AiSearchSession session : chunk.getContent()) {
            session.delete();
        }

        return chunk.getNumberOfElements();
    }
}
