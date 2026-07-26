package com.omo.backend.domain.inquiry.dto;

import com.omo.backend.domain.inquiry.enums.InquiryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class InquiryRequestDTO {

    // 1:1 문의
    public record InquiryDTO(
            @Schema(description = "문의 유형", example = "BUG_REPORT")
            @NotNull(message = "문의 유형은 필수 입력값입니다.")
            InquiryType type,

            @Schema(description = "문의자 이름", example = "홍길동")
            @NotBlank(message = "이름은 필수 입력값입니다.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
            String name,

            @Schema(description = "답변을 받을 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email,

            @Schema(description = "문의 내용", example = "로그인 중 오류가 발생합니다.")
            @NotBlank(message = "문의 내용은 필수 입력값입니다.")
            @Size(min = 10, max = 1000, message = "문의 내용은 10자 이상 1,000자 이하로 입력해 주세요.")
            String content,

            @Schema(description = "첨부파일 업로드 URL 발급 응답의 uploadToken")
            String uploadToken,

            @Schema(description = "S3 업로드를 완료한 임시 object key 목록")
            @Size(max = 3, message = "문의 이미지는 최대 3개까지 첨부할 수 있습니다.")
            List<@NotBlank(message = "첨부파일 object key는 공백일 수 없습니다.") @Size(max = 500, message = "첨부파일 object key는 500자 이하여야 합니다.") String> attachmentKeys
    ) {}

    // 문의 첨부파일 업로드 URL 일괄 발급
    public record AttachmentUploadUrlsDTO(
            @Schema(description = "업로드할 이미지 목록")
            @NotEmpty(message = "업로드할 이미지는 최소 1개 이상이어야 합니다.")
            @Size(max = 3, message = "문의 이미지는 최대 3개까지 업로드할 수 있습니다.")
            List<@Valid AttachmentFileDTO> files
    ) {}

    public record AttachmentFileDTO(
            @Schema(description = "업로드할 파일명", example = "error-screen.jpg")
            @NotBlank(message = "파일명은 필수 입력값입니다.")
            String fileName,

            @Schema(description = "파일 MIME 타입", example = "image/jpeg")
            @NotBlank(message = "파일 MIME 타입은 필수 입력값입니다.")
            String contentType,

            @Schema(description = "파일 크기(byte)", example = "1048576")
            @NotNull(message = "파일 크기는 필수 입력값입니다.")
            @Positive(message = "파일 크기는 0보다 커야 합니다.")
            @Max(value = 5L * 1024 * 1024, message = "파일 크기는 5MB 이하여야 합니다.")
            Long fileSize
    ) {}
}
