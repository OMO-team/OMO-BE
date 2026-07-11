package com.omo.backend.domain.member.controller;

import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "일반 회원가입", description = "이메일, 비밀번호, 이름, 약관 동의 목록으로 일반 회원가입을 진행합니다.")
    ApiResponse<MemberResponseDTO.JoinResultDTO> signup(
            @Valid @RequestBody MemberRequestDTO.JoinDTO request
    );
}
