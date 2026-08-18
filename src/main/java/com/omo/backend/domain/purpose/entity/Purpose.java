package com.omo.backend.domain.purpose.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.city.entity.CityPurpose;
import com.omo.backend.domain.purpose.enums.PurposeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@Table(name = "purpose")
public class Purpose extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purposeId;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PurposeEnum type;

    @Builder.Default
    @OneToMany(mappedBy = "purpose")
    private List<CityPurpose> cityPurposes = new ArrayList<>();



}
