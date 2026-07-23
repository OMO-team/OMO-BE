package com.omo.backend.domain.report.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.report.enums.ResourceTopic;
import com.omo.backend.domain.report.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "city_related_resource")
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityRelatedResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ResourceTopic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 20)
    private ResourceType resourceType;

    @Column(length = 200)
    private String title;

    @Column(length = 100)
    private String source;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private CityRelatedResource(Long cityId, ResourceTopic topic, ResourceType resourceType,
                                String title, String source, String url) {
        this.cityId = cityId;
        this.topic = topic;
        this.resourceType = resourceType;
        this.title = title;
        this.source = source;
        this.url = url;
    }
}