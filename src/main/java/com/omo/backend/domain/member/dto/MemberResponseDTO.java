package com.omo.backend.domain.member.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    // 일반 회원가입 결과
    @Builder
    public record JoinResultDTO(
            Long memberId,
            String name,
            LocalDateTime createdAt
    ) {}
}
