package com.omo.backend.domain.city.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.country.entity.Country;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@Table(name = "city")
public class City extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cityId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "monthly_cost")
    private Integer monthlyCost;

    @Column(name = "initial_settlement_cost")
    private Integer initialSettlementCost;

    @Column(name = "safety_score", precision = 2, scale = 1)
    private BigDecimal safetyScore;

    @Column(name = "visa_score", precision = 2, scale = 1)
    private BigDecimal visaScore;

    @Column(name = "housing_score", precision = 2, scale = 1)
    private BigDecimal housingScore;

    @Column(name = "infra_score", precision = 2, scale = 1)
    private BigDecimal infraScore;

// language_score >= 4.0을 영어권 도시 기준으로 사용
    @Column(name = "language_score", precision = 2, scale = 1)
    private BigDecimal languageScore;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Builder.Default
    @OneToMany(mappedBy = "city")
    private List<CityPurpose> cityPurposes = new ArrayList<>();
}
