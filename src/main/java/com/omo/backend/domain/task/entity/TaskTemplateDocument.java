package com.omo.backend.domain.task.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.document.entity.DocumentTemplate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
        name = "task_template_document",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_task_template_document",
                columnNames = {"task_template_id", "document_template_id"}
        )
)
public class TaskTemplateDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_template_id", nullable = false)
    private TaskTemplate taskTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_template_id", nullable = false)
    private DocumentTemplate documentTemplate;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
}
