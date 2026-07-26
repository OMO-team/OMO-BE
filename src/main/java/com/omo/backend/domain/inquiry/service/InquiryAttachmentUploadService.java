package com.omo.backend.domain.inquiry.service;

import com.omo.backend.domain.inquiry.converter.InquiryConverter;
import com.omo.backend.domain.inquiry.dto.InquiryRequestDTO;
import com.omo.backend.domain.inquiry.dto.InquiryResponseDTO;
import com.omo.backend.global.storage.FileValidationPolicy;
import com.omo.backend.global.storage.S3Properties;
import com.omo.backend.global.storage.enums.FileType;
import com.omo.backend.global.storage.service.S3PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InquiryAttachmentUploadService {

    private static final String TEMP_INQUIRY_KEY_FORMAT = "temp/inquiries/%s/%s.%s";

    private final FileValidationPolicy fileValidationPolicy;
    private final S3PresignedUrlService s3PresignedUrlService;
    private final S3Properties s3Properties;
    private final InquiryUploadSessionStore inquiryUploadSessionStore;

    public InquiryResponseDTO.AttachmentUploadUrlsResultDTO createUploadUrls(
            InquiryRequestDTO.AttachmentUploadUrlsDTO request
    ) {
        // 한 번에 발급한 첨부파일들을 묶고 문의 등록 시 검증할 일회성 업로드 토큰 생성
        String uploadToken = UUID.randomUUID().toString();
        List<InquiryResponseDTO.AttachmentUploadUrlDTO> uploads = new ArrayList<>(request.files().size());
        Map<String, String> originalNamesByObjectKey = new LinkedHashMap<>();

        for (InquiryRequestDTO.AttachmentFileDTO file : request.files()) {
            // 요청한 파일의 크기와 확장자 및 MIME 타입 조합을 검증
            FileType fileType = fileValidationPolicy.validateUploadRequest(file.fileName(), file.contentType(), file.fileSize());

            // 문의가 등록되기 전까지 사용할 uploadToken 기반 임시 object key 생성
            String objectKey = generateTempObjectKey(uploadToken, fileType);

            // 문의 첨부파일 버킷의 임시 경로에 직접 업로드할 수 있는 Presigned PUT URL 발급
            S3PresignedUrlService.PresignedPutUrl presignedPutUrl = s3PresignedUrlService.createPutUrl(
                    s3Properties.inquiryBucket(),
                    objectKey,
                    fileType.getContentType(),
                    file.fileSize()
            );

            // 문의 등록 시 발급된 key인지 검증하고 원본 파일명을 저장할 수 있도록 매핑 정보 보관
            originalNamesByObjectKey.put(objectKey, file.fileName());
            uploads.add(InquiryConverter.toAttachmentUploadUrlDTO(
                    presignedPutUrl.uploadUrl().toString(),
                    objectKey,
                    fileType.getContentType(),
                    presignedPutUrl.expiresAt()
            ));
        }

        // 업로드 토큰과 발급 정보를 URL 유효시간 동안 Redis에 저장
        inquiryUploadSessionStore.save(uploadToken, originalNamesByObjectKey, s3Properties.presignedUrlExpiration());

        // 프론트가 S3에 직접 업로드할 때 필요한 토큰과 파일별 업로드 정보를 반환
        return InquiryConverter.toAttachmentUploadUrlsResultDTO(uploadToken, uploads);
    }

    private String generateTempObjectKey(String uploadToken, FileType fileType) {
        // ex) temp/inquiries/{uploadToken}/550e8400-e29b-41d4-a716-446655440000.jpg
        return TEMP_INQUIRY_KEY_FORMAT.formatted(uploadToken, UUID.randomUUID(), fileType.getCanonicalExtension());
    }
}
