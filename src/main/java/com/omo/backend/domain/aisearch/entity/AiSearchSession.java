package com.omo.backend.domain.aisearch.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(mappedBy = "aiSearchSession", cascade = CascadeType.ALL)
    private List<AiSearchLog> searchLogs = new ArrayList<>();


}
