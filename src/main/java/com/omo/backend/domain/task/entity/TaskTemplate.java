package com.omo.backend.domain.task.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.roadmap.entity.RoadmapTemplate;
import com.omo.backend.domain.task.enums.TaskCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "task_template")
public class TaskTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_template_id", nullable = false)
    private RoadmapTemplate roadmapTemplate;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = false)
    private TaskCategory category;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "days_before_departure", nullable = false)
    private Integer daysBeforeDeparture;

    @Builder.Default
    @OneToMany(mappedBy = "taskTemplate")
    private List<Task> tasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "taskTemplate")
    private List<TaskTemplateDependency> dependencies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "prerequisiteTaskTemplate")
    private List<TaskTemplateDependency> prerequisiteFor = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "taskTemplate")
    private List<TaskTemplateDocument> documents = new ArrayList<>();
}
