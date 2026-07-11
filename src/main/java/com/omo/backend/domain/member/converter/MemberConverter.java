package com.omo.backend.domain.member.converter;

import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.MemberTerms;
import com.omo.backend.domain.member.entity.MemberSettings;
import com.omo.backend.domain.terms.entity.Terms;

import java.time.LocalDateTime;

public class MemberConverter {

    // DTO -> Member 엔티티
    public static Member toMember(MemberRequestDTO.JoinDTO request, String encodedPassword) {
        return Member.createLocalMember(request.email(), encodedPassword, request.name());
    }

    // DTO -> MemberSettings 엔티티
    public static MemberSettings toDefaultMemberSettings(Member member) {
        return MemberSettings.createDefaultSettings(member);
    }

    // DTO -> MemberTerms 엔티티
    public static MemberTerms toMemberTerms(Member member, Terms terms) {
        return MemberTerms.createMemberTerms(member, terms, LocalDateTime.now());
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
