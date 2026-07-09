package com.omo.backend.domain.terms.controller;

import com.omo.backend.domain.terms.dto.TermsResponseDTO;
import com.omo.backend.domain.terms.service.TermsQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terms")
public class TermsController implements TermsControllerDocs {

    private final TermsQueryService termsQueryService;

    @GetMapping
    public ApiResponse<TermsResponseDTO.TermsListDTO> getTermsList() {
        TermsResponseDTO.TermsListDTO result = termsQueryService.getTermsList();
        return ApiResponse.onSuccess(result);
    }
}
