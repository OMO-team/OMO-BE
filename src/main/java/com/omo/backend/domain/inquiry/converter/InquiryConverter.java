package com.omo.backend.domain.inquiry.converter;

import com.omo.backend.domain.inquiry.dto.InquiryRequestDTO;
import com.omo.backend.domain.inquiry.dto.InquiryResponseDTO;
import com.omo.backend.domain.inquiry.entity.Inquiry;
import com.omo.backend.domain.member.entity.Member;

import java.time.Instant;
import java.util.List;

public class InquiryConverter {

    // DTO -> Inquiry 엔티티
    public static Inquiry toInquiry(InquiryRequestDTO.InquiryDTO request, Member member) {
        return Inquiry.createInquiry(
                member,
                request.type(),
                request.name(),
                request.email(),
                request.content()
        );
    }

    // entity -> 1:1 문의 DTO
    public static InquiryResponseDTO.InquiryResultDTO toInquiryResultDTO(Inquiry inquiry) {
        return InquiryResponseDTO.InquiryResultDTO.builder()
                .inquiryId(inquiry.getId())
                .status(inquiry.getStatus())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }

    // 업로드 정보 -> 문의 첨부파일 업로드 URL DTO
    public static InquiryResponseDTO.AttachmentUploadUrlDTO toAttachmentUploadUrlDTO(
            String uploadUrl,
            String objectKey,
            String contentType,
            Instant expiresAt
    ) {
        return InquiryResponseDTO.AttachmentUploadUrlDTO.builder()
                .uploadUrl(uploadUrl)
                .objectKey(objectKey)
                .contentType(contentType)
                .expiresAt(expiresAt)
                .build();
    }

    // uploadToken 및 업로드 URL 목록 -> 문의 첨부파일 업로드 URL 일괄 발급 결과 DTO
    public static InquiryResponseDTO.AttachmentUploadUrlsResultDTO toAttachmentUploadUrlsResultDTO(
            String uploadToken,
            List<InquiryResponseDTO.AttachmentUploadUrlDTO> uploads
    ) {
        return InquiryResponseDTO.AttachmentUploadUrlsResultDTO.builder()
                .uploadToken(uploadToken)
                .uploads(uploads)
                .build();
    }
}
