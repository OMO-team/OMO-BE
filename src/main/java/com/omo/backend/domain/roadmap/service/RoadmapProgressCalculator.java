package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.task.entity.Task;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RoadmapProgressCalculator {

    public double calculate(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0.0;
        }

        long completedCount = tasks.stream()
                .filter(Task::isCompleted)
                .count();
        return completedCount * 100.0 / tasks.size();
    }
}
