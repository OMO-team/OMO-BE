package com.omo.backend.domain.task.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.entity.TaskDependency;
import com.omo.backend.domain.task.enums.TaskStatus;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusCalculator {

    public TaskStatus calculate(
            Task task,
            List<TaskDependency> dependencies,
            List<TaskDocument> documents
    ) {
        boolean hasIncompletePrerequisite = dependencies.stream()
                .map(TaskDependency::getPrerequisiteTask)
                .anyMatch(prerequisite -> !prerequisite.isCompleted());
        if (hasIncompletePrerequisite) {
            return TaskStatus.LOCKED;
        }

        if (task.isCompleted()) {
            return TaskStatus.COMPLETED;
        }

        boolean hasAnyCheckedDocument = documents.stream()
                .anyMatch(document -> Boolean.TRUE.equals(document.getChecked()));
        if (hasAnyCheckedDocument) {
            return TaskStatus.IN_PROGRESS;
        }

        return TaskStatus.PENDING;
    }
}
