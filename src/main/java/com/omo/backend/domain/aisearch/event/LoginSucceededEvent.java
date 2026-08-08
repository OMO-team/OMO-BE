package com.omo.backend.domain.aisearch.event;

public record LoginSucceededEvent(Long memberId, String guestSessionId) {}
