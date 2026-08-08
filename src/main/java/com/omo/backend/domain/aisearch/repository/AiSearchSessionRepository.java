package com.omo.backend.domain.aisearch.repository;

import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiSearchSessionRepository extends JpaRepository<AiSearchSession, Long> {
    // 삭제되지 않은 세션만 조회
    Optional<AiSearchSession> findByIdAndDeletedAtIsNull(Long id);

    // 지정 시간 이전 생성된 세션 조회 (스케줄러용)
    Slice<AiSearchSession> findAllByCreatedAtBeforeAndDeletedAtIsNull(LocalDateTime threshold, Pageable pageable);

    // 게스트 세션을 회원 계정으로 마이그레이션 (동시 로그인 시 중복 이전 방지)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE AiSearchSession s SET s.memberId = :memberId, s.guestSessionId = NULL " +
            "WHERE s.guestSessionId = :guestSessionId AND s.memberId IS NULL AND s.deletedAt IS NULL")
    int migrateGuestSessions(@Param("memberId") Long memberId, @Param("guestSessionId") String guestSessionId);
}
