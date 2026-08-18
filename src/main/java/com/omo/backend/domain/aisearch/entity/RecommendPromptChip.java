package com.omo.backend.domain.aisearch.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.global.apiPayload.code.GeneralErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommend_prompt_chip")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendPromptChip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "preset_query", nullable = false, length = 500)
    private String presetQuery;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public static RecommendPromptChip of(String title, String presetQuery) {
        if (title == null || title.isBlank()) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }
        if (presetQuery == null || presetQuery.isBlank()) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }

        return RecommendPromptChip.builder()
                .title(title)
                .presetQuery(presetQuery)
                .isActive(true)
                .build();
    }

    // 노출 제어를 위한 상태 변경 메서드
    public void updateActiveStatus(boolean isActive) {
        this.isActive = isActive;
    }
}
