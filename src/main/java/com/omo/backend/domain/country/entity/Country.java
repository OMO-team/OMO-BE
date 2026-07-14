package com.omo.backend.domain.country.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@Table(name = "country")
public class Country extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long countryId;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "code", length = 10, nullable = false, unique = true)
    private String code;

    @Column(name = "image_url", length = 500)
    private String imageUrl;


}
