package com.omo.backend.domain.city.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.purpose.entity.Purpose;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "city_purpose",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_city_purpose",
                        columnNames = {"city_id", "purpose_id"}
                )
        }
)
public class CityPurpose extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long cityPurposeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purpose_id")
    private Purpose purpose;
}
