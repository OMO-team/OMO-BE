package com.omo.backend.domain.document.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@DynamicInsert
@Table(name = "document_template")
public class DocumentTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "document_name", length = 100, nullable = false)
    private String documentName;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @Column(name = "ocr_support", nullable = false)
    private Boolean ocrSupport;

    public static DocumentTemplate createDocumentTemplate(
            String documentName,
            String description,
            Boolean ocrSupport
    ) {
        return DocumentTemplate.builder()
                .documentName(documentName)
                .description(description)
                .ocrSupport(ocrSupport)
                .build();
    }
}
