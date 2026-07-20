package com.omo.backend.domain.aisearch.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_search_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AiSearchLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_search_session_id", nullable = false)
    private AiSearchSession aiSearchSession;

    @Column(name = "search_query", nullable = false, length = 500)
    private String searchQuery;

    @Column(name = "is_refine", nullable = false)
    @Builder.Default
    private Boolean isRefine = false;

    @Column(name = "is_empty_result", nullable = false)
    @Builder.Default
    private Boolean isEmptyResult = false;

    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;


}
