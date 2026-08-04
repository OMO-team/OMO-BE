package com.omo.backend.domain.inquiry.controller;

import com.omo.backend.domain.inquiry.dto.InquiryRequestDTO;
import com.omo.backend.domain.inquiry.dto.InquiryResponseDTO;
import com.omo.backend.domain.inquiry.service.InquiryAttachmentUploadService;
import com.omo.backend.domain.inquiry.service.InquiryCommandService;
import com.omo.backend.domain.inquiry.service.InquiryUploadRateLimiter;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inquiries")
public class InquiryController implements InquiryControllerDocs {

    private final InquiryCommandService inquiryCommandService;
    private final InquiryAttachmentUploadService inquiryAttachmentUploadService;
    private final InquiryUploadRateLimiter inquiryUploadRateLimiter;

    @PostMapping
    public ApiResponse<InquiryResponseDTO.InquiryResultDTO> createInquiry(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InquiryRequestDTO.InquiryDTO request
    ) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        InquiryResponseDTO.InquiryResultDTO result = inquiryCommandService.createInquiry(memberId, request);
        return ApiResponse.created(result);
    }

    @PostMapping("/attachments/upload-urls")
    public ApiResponse<InquiryResponseDTO.AttachmentUploadUrlsResultDTO> createAttachmentUploadUrls(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody InquiryRequestDTO.AttachmentUploadUrlsDTO request
    ) {
        // 현재 EC2에 직접 연결되는 요청의 원격 IP를 기준으로 1분당 URL 발급 횟수 제한
        inquiryUploadRateLimiter.check(httpServletRequest.getRemoteAddr());

        InquiryResponseDTO.AttachmentUploadUrlsResultDTO result = inquiryAttachmentUploadService.createUploadUrls(request);
        return ApiResponse.onSuccess(result);
    }
}
