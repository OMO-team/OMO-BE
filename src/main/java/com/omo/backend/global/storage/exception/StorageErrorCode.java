package com.omo.backend.global.storage.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StorageErrorCode implements BaseErrorCode {

    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "STORAGE400_1", "지원하지 않는 파일 형식입니다."),
    INVALID_FILE_SIZE(HttpStatus.BAD_REQUEST, "STORAGE400_2", "파일 크기는 0바이트보다 크고 5MB 이하여야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
