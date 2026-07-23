package com.omo.backend.domain.roadmap.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class RoadmapScheduleCalculator {

    public Long calculateDDay(LocalDate date, LocalDate today) {
        if (date == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(today, date);
    }

    public Boolean isOverdue(LocalDate date, LocalDate today, boolean isCompleted) {
        if (date == null) {
            return null;
        }
        return !isCompleted && date.isBefore(today);
    }
}
