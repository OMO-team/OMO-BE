package com.omo.backend.domain.terms.controller;

import com.omo.backend.domain.terms.dto.TermsResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Terms", description = "약관 API")
public interface TermsControllerDocs {

    @Operation(summary = "약관 목록 조회", description = "회원가입 화면에서 표시할 약관 목록을 조회합니다. content는 Markdown 형식입니다.")
    ApiResponse<TermsResponseDTO.TermsListDTO> getTermsList();
}
