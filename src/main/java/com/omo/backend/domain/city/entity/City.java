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

    @Column(name = "name", length = 100,nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "monthly_cost")
    private Integer monthlyCost;

    @Column(name = "safety_score", precision = 2, scale = 1)
    private BigDecimal safetyScore;

    @Column(name = "visa_score", precision = 2, scale = 1)
    private BigDecimal visaScore;

    @Column(name = "housing_score", precision = 2, scale = 1)
    private BigDecimal housingScore;

    @Column(name = "infra_score", precision = 2, scale = 1)
    private BigDecimal infraScore;

