package com.omo.backend.domain.report.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;

@Entity
@Table(name = "city_core_summary")
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityCoreSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SummaryCategory category;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private CityCoreSummary(Long cityId, SummaryCategory category, String title, String content) {
        this.cityId = cityId;
        this.category = category;
        this.title = title;
        this.content = content;
    }
}
