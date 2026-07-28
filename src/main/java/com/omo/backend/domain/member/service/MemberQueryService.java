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
import com.omo.backend.global.storage.S3Properties;
import com.omo.backend.global.storage.service.S3PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final MemberSettingsRepository memberSettingsRepository;
    private final S3PresignedUrlService s3PresignedUrlService;
    private final S3Properties s3Properties;

    public MemberResponseDTO.MyInfoResultDTO getMyInfo(Long memberId) {
        Member member = getActiveMember(memberId);

        // 등록된 프로필 이미지가 없으면 이미지 조회 정보 없이 회원 정보만 반환
        if (member.getProfileImageKey() == null) {
            return MemberConverter.toMyInfoResultDTO(member, null, null);
        }

        // 비공개 S3 객체를 회원 본인만 제한된 시간 동안 조회할 수 있도록 Presigned GET URL 발급
        S3PresignedUrlService.PresignedGetUrl presignedGetUrl = s3PresignedUrlService.createGetUrl(s3Properties.profileBucket(), member.getProfileImageKey());

        // DB에는 object key만 유지하고, 클라이언트에는 임시 조회 URL과 만료 시각을 반환
        return MemberConverter.toMyInfoResultDTO(member, presignedGetUrl.imageUrl().toString(), presignedGetUrl.expiresAt());
    }

    public MemberResponseDTO.SettingsResultDTO getMySettings(Long memberId) {
        MemberSettings memberSettings = getMemberSettings(memberId);
        return MemberConverter.toSettingsResultDTO(memberSettings);
    }

    public void validateActiveMember(Long memberId) {
        getActiveMember(memberId);
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
