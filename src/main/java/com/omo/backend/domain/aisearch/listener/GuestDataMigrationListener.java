package com.omo.backend.domain.aisearch.listener;

import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import com.omo.backend.domain.aisearch.event.LoginSucceededEvent;
import com.omo.backend.domain.aisearch.repository.AiSearchSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuestDataMigrationListener {

    private final AiSearchSessionRepository aiSearchSessionRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onLoginSucceeded(LoginSucceededEvent event) {
        if (event.guestSessionId() == null) {
            log.warn("guestSessionId가 null이라 마이그레이션 스킵");
            return;
        }

        int migratedCount = aiSearchSessionRepository.migrateGuestSessions(event.memberId(), event.guestSessionId());
        log.info("마이그레이션 완료 : {}", migratedCount);
    }
}