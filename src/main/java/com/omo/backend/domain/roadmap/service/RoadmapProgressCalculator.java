package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RoadmapProgressCalculator {

    public double calculate(List<TaskDocument> documents) {
        if (documents.isEmpty()) {
            return 0.0;
        }

        long checkedCount = documents.stream()
                .filter(document -> Boolean.TRUE.equals(document.getChecked()))
                .count();
        return checkedCount * 100.0 / documents.size();
    }
}
