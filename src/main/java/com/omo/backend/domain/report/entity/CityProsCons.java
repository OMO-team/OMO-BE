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
@Table(name = "city_pros_cons")
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CityProsCons extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "city_id")
    private Long cityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ProsConsType type;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private CityProsCons(Long cityId, ProsConsType type, String content,Integer displayOrder){
        this.cityId = cityId;
        this.type = type;
        this.content = content;
        this.displayOrder = displayOrder;
    }
}
