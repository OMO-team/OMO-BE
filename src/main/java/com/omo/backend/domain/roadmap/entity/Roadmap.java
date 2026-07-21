package com.omo.backend.domain.roadmap.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.budget.entity.Budget;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.task.entity.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
    @JoinColumn(name = "roadmap_template_id", nullable = false)
    private RoadmapTemplate roadmapTemplate;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "stay_months", nullable = false)
    private Integer stayMonths;

    @Builder.Default
    @OneToMany(mappedBy = "roadmap")
    private List<Task> tasks = new ArrayList<>();

    @OneToOne(mappedBy = "roadmap", fetch = FetchType.LAZY)
    private Budget budget;

    public static Roadmap create(
            Member member,
            RoadmapTemplate roadmapTemplate,
            String title,
            LocalDate departureDate,
            Integer stayMonths
    ) {
        return Roadmap.builder()
                .member(member)
                .roadmapTemplate(roadmapTemplate)
                .title(title)
                .departureDate(departureDate)
                .stayMonths(stayMonths)
                .build();
    }
}
