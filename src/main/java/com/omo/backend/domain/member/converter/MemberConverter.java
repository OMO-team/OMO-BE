package com.omo.backend.domain.member.converter;

import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.MemberTerms;
import com.omo.backend.domain.member.entity.MemberSettings;
import com.omo.backend.domain.terms.entity.Terms;

import java.time.Instant;
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

    // entity -> 내 정보 조회 DTO
    public static MemberResponseDTO.MyInfoResultDTO toMyInfoResultDTO(Member member) {
        return MemberResponseDTO.MyInfoResultDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .profileImageKey(member.getProfileImageKey())
                .provider(member.getProvider())
                .build();
    }

    // entity -> 프로필 수정 DTO
    public static MemberResponseDTO.UpdateProfileResultDTO toUpdateProfileResultDTO(Member member) {
        return MemberResponseDTO.UpdateProfileResultDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();
    }

    // 업로드 정보 -> 프로필 이미지 업로드 URL 발급 DTO
    public static MemberResponseDTO.ProfileImageUploadUrlResultDTO toProfileImageUploadUrlResultDTO(
            String uploadUrl,
            String objectKey,
            String contentType,
            Instant expiresAt
    ) {
        return MemberResponseDTO.ProfileImageUploadUrlResultDTO.builder()
                .uploadUrl(uploadUrl)
                .objectKey(objectKey)
                .contentType(contentType)
                .expiresAt(expiresAt)
                .build();
    }

    // entity -> 프로필 이미지 등록 DTO
    public static MemberResponseDTO.ProfileImageUpdateResultDTO toProfileImageUpdateResultDTO(Member member) {
        return MemberResponseDTO.ProfileImageUpdateResultDTO.builder()
                .memberId(member.getId())
                .objectKey(member.getProfileImageKey())
                .build();
    }

    // entity -> 내 설정 조회/수정 DTO
    public static MemberResponseDTO.SettingsResultDTO toSettingsResultDTO(MemberSettings memberSettings) {
        return MemberResponseDTO.SettingsResultDTO.builder()
                .pushNotification(memberSettings.getPushNotification())
                .emailNotification(memberSettings.getEmailNotification())
                .autoSave(memberSettings.getAutoSave())
                .twoFactorEnabled(memberSettings.getTwoFactorEnabled())
                .build();
    }
}
