package com.omo.backend.domain.member.converter;

import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.MemberTerms;
import com.omo.backend.domain.member.enums.MemberProvider;
import com.omo.backend.domain.member.entity.MemberSettings;
import com.omo.backend.domain.member.enums.MemberStatus;
import com.omo.backend.domain.terms.entity.Terms;

import java.time.LocalDateTime;

public class MemberConverter {

    // DTO -> Member 엔티티
    public static Member toMember(MemberRequestDTO.JoinDTO request, String encodedPassword) {
        return Member.builder()
                .name(request.name())
                .email(request.email())
                .password(encodedPassword)
                .provider(MemberProvider.LOCAL)
                .emailVerified(false)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    // DTO -> MemberSettings 엔티티
    public static MemberSettings toDefaultMemberSettings(Member member) {
        return MemberSettings.builder()
                .member(member)
                .pushNotification(true)
                .emailNotification(true)
                .autoSave(true)
                .twoFactorEnabled(false)
                .build();
    }

    // DTO -> MemberTerms 엔티티
    public static MemberTerms toMemberTerms(Member member, Terms terms) {
        return MemberTerms.builder()
                .member(member)
                .terms(terms)
                .agreedAt(LocalDateTime.now())
                .build();
    }

    // entity -> 일반 회원가입 DTO
    public static MemberResponseDTO.JoinResultDTO toJoinResultDTO(Member member) {
        return MemberResponseDTO.JoinResultDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
