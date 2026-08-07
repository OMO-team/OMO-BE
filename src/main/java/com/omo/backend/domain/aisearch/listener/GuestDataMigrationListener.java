package com.omo.backend.domain.aisearch.listener;

import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import com.omo.backend.domain.aisearch.event.LoginSucceededEvent;
import com.omo.backend.domain.aisearch.repository.AiSearchSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GuestDataMigrationListener {

    private final AiSearchSessionRepository aiSearchSessionRepository;

    @TransactionalEventListener
    public void onLoginSucceeded(LoginSucceededEvent event) {
        if (event.guestSessionId() == null) return;
        List<AiSearchSession> sessions = aiSearchSessionRepository.findAllByGuestSessionIdAndDeletedAtIsNull(event.guestSessionId());
        sessions.forEach(s -> s.migrateToMember(event.memberId()));
    }
}