package com.omo.backend.domain.member.service;

import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.global.storage.FileValidationPolicy;
import com.omo.backend.global.storage.enums.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageUploadService {

    private static final String PROFILE_IMAGE_KEY_FORMAT = "profiles/%d/%s.%s";

    private final FileValidationPolicy fileValidationPolicy;

    public String createObjectKey(Long memberId, MemberRequestDTO.ProfileImageUploadUrlDTO request) {
        FileType fileType = fileValidationPolicy.validateUploadRequest(request.fileName(), request.contentType(), request.fileSize());

        return generateProfileKey(memberId, fileType);
    }

    private String generateProfileKey(Long memberId, FileType fileType) {
        return PROFILE_IMAGE_KEY_FORMAT.formatted(memberId, UUID.randomUUID(), fileType.getCanonicalExtension());
    }
}
