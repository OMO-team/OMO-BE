package com.omo.backend.domain.auth.service;

import com.omo.backend.global.storage.FileValidationPolicy;
import com.omo.backend.global.storage.S3Properties;
import com.omo.backend.global.storage.enums.FileType;
import com.omo.backend.global.storage.exception.StorageException;
import com.omo.backend.global.storage.service.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleProfileImageService {

    private static final String GOOGLE_PROFILE_KEY_FORMAT = "profiles/%d/google-%s.%s";

    private final S3FileService s3FileService;
    private final FileValidationPolicy fileValidationPolicy;
    private final S3Properties s3Properties;

    // Google 프로필 이미지를 내려받아 프로젝트 프로필 버킷에 저장하고 object key를 반환
    public String upload(Long memberId, String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return null;
        }

        // 이미지 다운로드
        try {
            ResponseEntity<byte[]> response = RestClient.create()
                    .get()
                    .uri(imageUrl)
                    .retrieve()
                    .toEntity(byte[].class);

            // 이미지 데이터 추출
            byte[] image = response.getBody();
            String contentType = response.getHeaders().getContentType() == null ? null : response.getHeaders().getContentType().toString();

            if (image == null) {
                return null;
            }

            FileType fileType = fileValidationPolicy.validateExternalImage(contentType, image.length);
            String objectKey = GOOGLE_PROFILE_KEY_FORMAT.formatted(memberId, UUID.randomUUID(), fileType.getCanonicalExtension());

            s3FileService.upload(s3Properties.profileBucket(), objectKey, fileType.getContentType(), image);
            return objectKey;
        } catch (RestClientException | StorageException | IllegalArgumentException exception) {
            // 기본 프로필 이미지는 선택 정보이므로 실패해도 Google 회원가입은 계속 진행
            log.warn("Google 프로필 이미지 저장 실패: memberId={}", memberId, exception);
            return null;
        }
    }

    // 프로필 이미지 Object Key DB 반영 실패 시 업로드된 S3 객체 보상 삭제
    public void delete(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return;
        }

        try {
            s3FileService.delete(s3Properties.profileBucket(), objectKey);
        } catch (StorageException exception) {
            // 선택 이미지 정리 실패가 Google 회원가입 결과에 영향을 주지 않도록 경고만 기록
            log.warn("Google 프로필 이미지 보상 삭제 실패: objectKey={}", objectKey, exception);
        }
    }
}
