package com.omo.backend.domain.aisearch.repository;

import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiSearchSessionRepository extends JpaRepository<AiSearchSession, Long> {
    // 삭제되지 않은 세션만 조회
    Optional<AiSearchSession> findByIdAndDeletedAtIsNull(Long id);

    // 지정 시간 이전 생성된 세션 조회 (스케줄러용)
    Slice<AiSearchSession> findAllByCreatedAtBeforeAndDeletedAtIsNull(LocalDateTime threshold, Pageable pageable);

    // 게스트 세션 Id로 활성화된 세션 전체 조회
    List<AiSearchSession> findAllByGuestSessionIdAndDeletedAtIsNull(String guestSessionId);
}
