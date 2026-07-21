package com.omo.backend.domain.budget.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "budget")
public class Budget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false, unique = true)
    private Roadmap roadmap;

    @Column(name = "initial_cost", nullable = false)
    private Long initialCost;

    @Column(name = "monthly_cost", nullable = false)
    private Long monthlyCost;

    public static Budget create(Roadmap roadmap, Long initialCost, Long monthlyCost) {
        return Budget.builder()
                .roadmap(roadmap)
                .initialCost(initialCost)
                .monthlyCost(monthlyCost)
                .build();
    }

    public long calculateTotalCost() {
        return initialCost + monthlyCost * roadmap.getStayMonths();
    }
}
