package com.omo.backend.domain.inquiry.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "inquiry_attachment")
public class InquiryAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @Column(name = "original_name", length = 255, nullable = false)
    private String originalName;

    @Column(name = "stored_name", length = 255, nullable = false)
    private String storedName;

    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;

    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    public static InquiryAttachment createInquiryAttachment(
            Inquiry inquiry,
            String originalName,
            String storedName,
            String fileUrl,
            String contentType,
            Long fileSize
    ) {
        return InquiryAttachment.builder()
                .inquiry(inquiry)
                .originalName(originalName)
                .storedName(storedName)
                .fileUrl(fileUrl)
                .contentType(contentType)
                .fileSize(fileSize)
                .build();
    }
}
