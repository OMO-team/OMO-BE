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
import java.util.List;
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
        String uploadToken = UUID.randomUUID().toString();
        List<InquiryResponseDTO.AttachmentUploadUrlDTO> uploads = new ArrayList<>(request.files().size());
        List<String> objectKeys = new ArrayList<>(request.files().size());

        for (InquiryRequestDTO.AttachmentFileDTO file : request.files()) {
            FileType fileType = fileValidationPolicy.validateUploadRequest(
                    file.fileName(),
                    file.contentType(),
                    file.fileSize()
            );
            String objectKey = generateTempObjectKey(uploadToken, fileType);
            S3PresignedUrlService.PresignedPutUrl presignedPutUrl = s3PresignedUrlService.createPutUrl(
                    s3Properties.inquiryBucket(),
                    objectKey,
                    fileType.getContentType(),
                    file.fileSize()
            );

            objectKeys.add(objectKey);
            uploads.add(InquiryConverter.toAttachmentUploadUrlDTO(
                    presignedPutUrl.uploadUrl().toString(),
                    objectKey,
                    fileType.getContentType(),
                    presignedPutUrl.expiresAt()
            ));
        }

        inquiryUploadSessionStore.save(uploadToken, objectKeys, s3Properties.presignedUrlExpiration());
        return InquiryConverter.toAttachmentUploadUrlsResultDTO(uploadToken, uploads);
    }

    private String generateTempObjectKey(String uploadToken, FileType fileType) {
        return TEMP_INQUIRY_KEY_FORMAT.formatted(
                uploadToken,
                UUID.randomUUID(),
                fileType.getCanonicalExtension()
        );
    }
}
