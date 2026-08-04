package com.omo.backend.domain.inquiry.controller;

import com.omo.backend.domain.inquiry.dto.InquiryRequestDTO;
import com.omo.backend.domain.inquiry.dto.InquiryResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Inquiry", description = "1:1 문의 API")
public interface InquiryControllerDocs {

    @Operation(
            summary = "1:1 문의 등록",
            description = "로그인 여부와 관계없이 문의 유형, 이름, 이메일, 문의 내용으로 1:1 문의를 등록합니다. 로그인한 경우 회원 정보가 문의에 연결됩니다."
    )
    ApiResponse<InquiryResponseDTO.InquiryResultDTO> createInquiry(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InquiryRequestDTO.InquiryDTO request
    );

    @Operation(
            summary = "문의 첨부파일 업로드 URL 일괄 발급",
            description = """
                    문의에 첨부할 이미지를 S3에 직접 업로드할 수 있는 임시 PUT URL을 최대 3개까지 발급합니다.

                    응답의 각 uploadUrl로 파일을 PUT 업로드해야 합니다.
                    PUT 요청의 Content-Type은 응답의 contentType과 동일해야 하며,
                    요청 본문의 크기는 업로드 URL 발급 요청에 전달한 fileSize와 정확히 일치해야 합니다.
                    업로드가 완료되면 uploadToken과 objectKey 목록을 문의 등록 API에 전달해야 합니다.

                    클라이언트 IP당 1분에 최대 5회까지 요청할 수 있으며,
                    초과하면 429(INQUIRY429_1) 오류를 반환합니다.
                    """
    )
    ApiResponse<InquiryResponseDTO.AttachmentUploadUrlsResultDTO> createAttachmentUploadUrls(
            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody InquiryRequestDTO.AttachmentUploadUrlsDTO request
    );
}
