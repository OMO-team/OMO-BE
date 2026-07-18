package com.omo.backend.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_compare_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_compare_item",
                columnNames = {"member_id", "city_id"}
        ))
@Getter
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCompareItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id",  nullable = false)
    private Long memberId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @org.springframework.data.annotation.CreatedDate
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberCompareItem(Long memberId, Long cityId) {
        this.memberId = memberId;
        this.cityId = cityId;
    }
}
