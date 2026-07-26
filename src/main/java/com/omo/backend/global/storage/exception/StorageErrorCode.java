package com.omo.backend.global.storage.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StorageErrorCode implements BaseErrorCode {

    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "STORAGE400_1", "지원하지 않는 파일 형식입니다."),
    INVALID_FILE_SIZE(HttpStatus.BAD_REQUEST, "STORAGE400_2", "파일 크기는 0바이트보다 크고 5MB 이하여야 합니다."),
    INVALID_OBJECT_KEY(HttpStatus.BAD_REQUEST, "STORAGE400_3", "유효하지 않은 파일 경로입니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORAGE404_1", "업로드된 파일을 찾을 수 없습니다."),
    PRESIGNED_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE500_1", "파일 URL 생성 중 오류가 발생했습니다."),
    S3_OPERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE500_2", "파일 저장소 처리 중 오류가 발생했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
