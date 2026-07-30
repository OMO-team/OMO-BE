package com.omo.backend.domain.inquiry.repository;

import com.omo.backend.domain.inquiry.entity.InquiryAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryAttachmentRepository extends JpaRepository<InquiryAttachment, Long> {
}
