package com.omo.backend.domain.aisearch.event;

public record AiBriefingRequestedEvent(
            String taskId,
            Long sessionId,
            String searchQuery,
            Boolean isRefine
) {}
