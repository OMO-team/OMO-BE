package com.omo.backend.domain.member.dto;

import com.omo.backend.domain.member.enums.MemberProvider;
import com.omo.backend.domain.member.enums.MemberStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    // 일반 회원가입 결과
    @Builder
    public record JoinResultDTO(
            Long memberId,
            String name,
            LocalDateTime createdAt
    ) {}

    // 내 정보 조회 결과
    @Builder
    public record MyInfoResultDTO(
            Long memberId,
            String name,
            String email,
            String profileImageUrl,
            MemberProvider provider
    ) {}

    // 프로필 수정 결과
    @Builder
    public record UpdateProfileResultDTO(
            Long memberId,
            String name,
            String profileImageUrl
    ) {}

    // 내 설정 조회/수정 결과
    @Builder
    public record SettingsResultDTO(
            Boolean pushNotification,
            Boolean emailNotification,
            Boolean autoSave,
            Boolean twoFactorEnabled
    ) {}
}
