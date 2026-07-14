package com.omo.backend.domain.roadmap.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.budget.entity.Budget;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "roadmap")
public class Roadmap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // TODO: 도시 도메인 FK - City 엔티티 아직 미완성. 임시로 Long으로 처리.
    @Column(name = "city_id", nullable = false)
    private Long cityId;

    // TODO: 목적 도메인 FK - Purpose 엔티티 아직 미완성. 임시로 Long처리.
    @Column(name = "purpose_id", nullable = false)
    private Long purposeId;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "stay_months")
    private Integer stayMonths;

    // 대시보드 활성화 여부
    @Column(name = "is_active")
    private Boolean isActive;

    @Builder.Default
    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @OneToOne(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    private Budget budget;

    // === 정적 팩토리 메서드 ===

    public static Roadmap createRoadmap(Member member, Long cityId, Long purposeId,
                                        LocalDate departureDate, Integer stayMonths) {
        return Roadmap.builder()
                .member(member)
                .cityId(cityId)
                .purposeId(purposeId)
                .departureDate(departureDate)
                .stayMonths(stayMonths)
                .isActive(true)
                .build();
    }

    // === 생성자 ===
    @Builder
    public Roadmap(Member member, Long cityId, Long purposeId,
                   LocalDate departureDate, Integer stayMonths, Boolean isActive) {
        this.member = member;
        this.cityId = cityId;
        this.purposeId = purposeId;
        this.departureDate = departureDate;
        this.stayMonths = stayMonths;
        this.isActive = isActive;
    }

    // === 연관관계 편의 메서드 ===
    public void assignBudget(Budget budget) {
        this.budget = budget;
        if (budget != null && budget.getRoadmap() != this) {
            budget.assignRoadmap(this);
        }
    }

    // === 비즈니스 메서드 (수정자) ===
    public void updateDeparture(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public void updateStayMonths(Integer stayMonths) {
        this.stayMonths = stayMonths;
    }

    public void deactivate() {
        this.isActive = false;
    }
}