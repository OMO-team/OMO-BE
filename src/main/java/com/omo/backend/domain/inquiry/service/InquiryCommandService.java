package com.omo.backend.domain.inquiry.service;

import com.omo.backend.domain.inquiry.converter.InquiryConverter;
import com.omo.backend.domain.inquiry.dto.InquiryRequestDTO;
import com.omo.backend.domain.inquiry.dto.InquiryResponseDTO;
import com.omo.backend.domain.inquiry.entity.Inquiry;
import com.omo.backend.domain.inquiry.repository.InquiryRepository;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryCommandService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    public InquiryResponseDTO.InquiryResultDTO createInquiry(Long memberId, InquiryRequestDTO.InquiryDTO request) {
        Member member = findMemberIfPresent(memberId);

        Inquiry inquiry = inquiryRepository.save(InquiryConverter.toInquiry(request, member));
        return InquiryConverter.toInquiryResultDTO(inquiry);
    }

    private Member findMemberIfPresent(Long memberId) {
        if (memberId == null) {
            return null;
        }

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
