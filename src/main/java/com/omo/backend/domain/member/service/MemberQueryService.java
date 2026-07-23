package com.omo.backend.domain.member.service;

import com.omo.backend.domain.member.converter.MemberConverter;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.MemberSettings;
import com.omo.backend.domain.member.enums.MemberStatus;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.member.repository.MemberSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final MemberSettingsRepository memberSettingsRepository;

    public MemberResponseDTO.MyInfoResultDTO getMyInfo(Long memberId) {
        Member member = getActiveMember(memberId);
        return MemberConverter.toMyInfoResultDTO(member);
    }

    public MemberResponseDTO.SettingsResultDTO getMySettings(Long memberId) {
        MemberSettings memberSettings = getMemberSettings(memberId);
        return MemberConverter.toSettingsResultDTO(memberSettings);
    }

    private Member getActiveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        return member;
    }

    private MemberSettings getMemberSettings(Long memberId) {
        getActiveMember(memberId);
        return memberSettingsRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_SETTINGS_NOT_FOUND));
    }
}
