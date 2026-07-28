package com.omo.backend.domain.aisearch.repository;

import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiSearchSessionRepository extends JpaRepository<AiSearchSession, Long> {
    // 삭제되지 않은 세션만 조회
    Optional<AiSearchSession> findByIdAndDeletedAtIsNull(Long id);

    // 지정 시간 이전 생성된 세션 조회 (스케줄러용)
    List<AiSearchSession> findAllByCreatedAtBeforeAndDeletedAtIsNull(LocalDateTime threshold);
}
