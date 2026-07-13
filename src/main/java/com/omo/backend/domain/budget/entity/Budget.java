package com.omo.backend.domain.budget.entity;

import com.omo.backend.domain.exchangerate.entity.ExchangeRate;
import com.omo.backend.domain.exchangerate.enums.CurrencyCode;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로드맵 도메인 FK - 로드맵 1 : 예산 1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false, unique = true)
    private Roadmap roadmap;

    // 환율 도메인 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_rate_id", nullable = false)
    private ExchangeRate exchangeRate;

    // 초기비용 (화폐 단위에 따른 확장성을 위해 Long 권장)
    @Column(name = "initial_cost")
    private Long initialCost;

    // 월 비용
    @Column(name = "monthly_cost")
    private Long monthlyCost;

    // 화폐 단위
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code")
    private CurrencyCode currencyCode;

    // === 생성자 ===
    @Builder
    public Budget(Roadmap roadmap, ExchangeRate exchangeRate,
                  Long initialCost, Long monthlyCost, CurrencyCode currencyCode) {
        this.roadmap = roadmap;
        this.exchangeRate = exchangeRate;
        this.initialCost = initialCost;
        this.monthlyCost = monthlyCost;
        this.currencyCode = currencyCode;
    }

    // === 연관관계 편의 메서드 ===
    public void assignRoadmap(Roadmap roadmap) {
        this.roadmap = roadmap;
        if (roadmap != null && roadmap.getBudget() != this) {
            roadmap.assignBudget(this);
        }
    }

    // === 비즈니스 메서드 (수정자) ===
    public void updateCosts(Long initialCost, Long monthlyCost) {
        this.initialCost = initialCost;
        this.monthlyCost = monthlyCost;
    }

    public void updateCurrencyCode(CurrencyCode currencyCode) {
        this.currencyCode = currencyCode;
    }
}