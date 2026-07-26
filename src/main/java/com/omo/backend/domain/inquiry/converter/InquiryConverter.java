package com.omo.backend.domain.inquiry.converter;

import com.omo.backend.domain.inquiry.dto.InquiryRequestDTO;
import com.omo.backend.domain.inquiry.dto.InquiryResponseDTO;
import com.omo.backend.domain.inquiry.entity.Inquiry;
import com.omo.backend.domain.member.entity.Member;

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
}
