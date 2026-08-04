package com.omo.backend.domain.inquiry.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InquiryErrorCode implements BaseErrorCode {

    INVALID_ATTACHMENT_REQUEST(HttpStatus.BAD_REQUEST, "INQUIRY400_1", "문의 첨부파일 요청이 올바르지 않습니다."),
    INVALID_UPLOAD_TOKEN(HttpStatus.BAD_REQUEST, "INQUIRY400_2", "유효하지 않거나 만료된 업로드 토큰입니다."),
    DUPLICATE_ATTACHMENT(HttpStatus.BAD_REQUEST, "INQUIRY400_3", "중복된 문의 첨부파일이 포함되어 있습니다."),
    UPLOAD_TOKEN_IN_USE(HttpStatus.CONFLICT, "INQUIRY409_1", "이미 처리 중인 업로드 토큰입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
