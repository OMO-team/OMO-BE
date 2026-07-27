package com.omo.backend.domain.member.dto;

import com.omo.backend.domain.member.enums.MemberProvider;
import lombok.Builder;

import java.time.Instant;
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
            Instant profileImageUrlExpiresAt,
            MemberProvider provider
    ) {}

    // 프로필 수정 결과
    @Builder
    public record UpdateProfileResultDTO(
            Long memberId,
            String name
    ) {}

    // 프로필 이미지 업로드 URL 발급 결과
    @Builder
    public record ProfileImageUploadUrlResultDTO(
            String uploadUrl,
            String objectKey,
            String contentType,
            Instant expiresAt
    ) {}

    // 프로필 이미지 등록 결과
    @Builder
    public record ProfileImageUpdateResultDTO(
            Long memberId,
            String objectKey
    ) {}

    // 내 설정 조회/수정 결과
    @Builder
    public record SettingsResultDTO(
            Boolean pushNotification,
            Boolean emailNotification,
            Boolean autoSave
    ) {}
}
