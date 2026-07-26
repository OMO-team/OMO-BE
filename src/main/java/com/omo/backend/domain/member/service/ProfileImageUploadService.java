package com.omo.backend.domain.member.service;

import com.omo.backend.domain.member.converter.MemberConverter;
import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.global.storage.FileValidationPolicy;
import com.omo.backend.global.storage.S3Properties;
import com.omo.backend.global.storage.enums.FileType;
import com.omo.backend.global.storage.service.S3PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageUploadService {

    private static final String PROFILE_IMAGE_KEY_FORMAT = "profiles/%d/%s.%s";

    private final FileValidationPolicy fileValidationPolicy;
    private final S3PresignedUrlService s3PresignedUrlService;
    private final S3Properties s3Properties;
    private final MemberQueryService memberQueryService;

    public MemberResponseDTO.ProfileImageUploadUrlResultDTO createUploadUrl(Long memberId, MemberRequestDTO.ProfileImageUploadUrlDTO request) {
        // 탈퇴 또는 존재하지 않는 회원에게는 업로드 URL을 발급하지 않음
        memberQueryService.validateActiveMember(memberId);

        // 요청한 파일의 크기와 확장자 및 MIME 타입 조합을 검증
        FileType fileType = fileValidationPolicy.validateUploadRequest(request.fileName(), request.contentType(), request.fileSize());

        // 회원별 경로와 UUID를 사용해 S3에 저장할 고유 object key를 생성
        String objectKey = generateProfileKey(memberId, fileType);

        // 프로필 이미지 버킷의 해당 object key에 업로드할 수 있는 임시 PUT URL을 발급
        S3PresignedUrlService.PresignedPutUrl presignedPutUrl = s3PresignedUrlService.createPutUrl(
                s3Properties.profileBucket(),
                objectKey,
                fileType.getContentType(),
                request.fileSize()
        );

        // 프론트가 직접 업로드할 때 필요한 URL, key, Content-Type, 만료시각을 반환
        return MemberConverter.toProfileImageUploadUrlResultDTO(
                presignedPutUrl.uploadUrl().toString(),
                objectKey,
                fileType.getContentType(),
                presignedPutUrl.expiresAt()
        );
    }

    private String generateProfileKey(Long memberId, FileType fileType) {
        // ex) profiles/12/550e8400-e29b-41d4-a716-446655440000.jpg
        return PROFILE_IMAGE_KEY_FORMAT.formatted(memberId, UUID.randomUUID(), fileType.getCanonicalExtension());
    }
}
