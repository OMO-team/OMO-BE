package com.omo.backend.domain.aisearch.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_search_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AiSearchSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단순 id만 필요해서 연관관계 생략
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "accumulated_conditions", nullable = false, columnDefinition = "TEXT")
    private String accumulatedConditions;

    @Column(name = "guest_session_id", length = 36)
    private String guestSessionId;

    @OneToMany(mappedBy = "aiSearchSession", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AiSearchLog> searchLogs = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 누적 조건 업데이트 메서드
    public void updateAccumulatedConditions(String newConditions) {
        this.accumulatedConditions = newConditions;
    }

    // 세 세션 생성 메서드
    public static AiSearchSession createSession(Long memberId, String guestSessionId) {
        return AiSearchSession.builder()
                .memberId(memberId)
                .guestSessionId(memberId == null ? guestSessionId : null)
                .accumulatedConditions("{}")
                .build();
    }

    public boolean isOwnedBy(Long memberId, String guestSessionId) {
        if (memberId != null) {
            return memberId.equals(this.memberId);
        }
        return guestSessionId != null && guestSessionId.equals(this.guestSessionId);
    }

    public void migrateToMember(Long memberId) {
        this.memberId = memberId;
        this.guestSessionId = null;
    }

    // 세션 삭제 메서드
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
