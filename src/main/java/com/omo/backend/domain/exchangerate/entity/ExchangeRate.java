package com.omo.backend.domain.exchangerate.entity;

import com.omo.backend.domain.exchangerate.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "exchange_rate")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 환 코드 (JPY, USD 등등)
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false)
    private CurrencyCode currencyCode;

    // 원화 환산 기준 환율, 부동소수점 오차 방지용 bigdecimal
    @Column(name = "base_rate", nullable = false, precision = 15, scale = 4)
    private BigDecimal baseRate;

    // 외부 환율 API에서 내려주는 갱신 시각을 직접 관리
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 생성자 ===
    @Builder
    public ExchangeRate(CurrencyCode currencyCode, BigDecimal baseRate, LocalDateTime updatedAt) {
        this.currencyCode = currencyCode;
        this.baseRate = baseRate;
        this.updatedAt = updatedAt;
    }

    // === 비즈니스 메서드 (수정자) ===
    public void updateRate(BigDecimal baseRate, LocalDateTime updatedAt) {
        this.baseRate = baseRate;
        this.updatedAt = updatedAt;
    }
}
