package com.omo.backend.domain.roadmap.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.purpose.entity.Purpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "roadmap")
public class Roadmap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purpose_id", nullable = false)
    private Purpose purpose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_template_id", nullable = false)
    private RoadmapTemplate roadmapTemplate;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    public static Roadmap create(
            Member member,
            City city,
            Purpose purpose,
            RoadmapTemplate roadmapTemplate,
            String title,
            LocalDate departureDate
    ) {
        return Roadmap.builder()
                .member(member)
                .city(city)
                .purpose(purpose)
                .roadmapTemplate(roadmapTemplate)
                .title(title)
                .departureDate(departureDate)
                .build();
    }
}
