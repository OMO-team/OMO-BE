package com.omo.backend.domain.roadmap.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.purpose.entity.Purpose;
import com.omo.backend.domain.task.entity.TaskTemplate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "roadmap_template")
public class RoadmapTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purpose_id", nullable = false)
    private Purpose purpose;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "roadmapTemplate")
    private List<TaskTemplate> taskTemplates = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "roadmapTemplate")
    private List<Roadmap> roadmaps = new ArrayList<>();
}
