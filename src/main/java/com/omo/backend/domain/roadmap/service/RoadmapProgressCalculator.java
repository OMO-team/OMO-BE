package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.task.entity.Task;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RoadmapProgressCalculator {

    public double calculate(
            List<Task> tasks,
            Map<Long, List<TaskDocument>> documentsByTaskId
    ) {
        if (tasks == null || tasks.isEmpty()) {
            return 0.0;
        }

        double totalProgressScore = 0.0;

        for (Task task : tasks) {
            List<TaskDocument> documents = documentsByTaskId == null
                    ? Collections.emptyList()
                    : documentsByTaskId.get(task.getId());

            if (documents == null || documents.isEmpty()) {
                totalProgressScore += task.isCompleted() ? 1.0 : 0.0;
                continue;
            }

            long checkedCount = documents.stream()
                    .filter(document -> Boolean.TRUE.equals(document.getChecked()))
                    .count();
            totalProgressScore += (double) checkedCount / documents.size();
        }

        double progressRate = totalProgressScore / tasks.size() * 100.0;
        return Math.max(0.0, Math.min(100.0, progressRate));
    }
}
