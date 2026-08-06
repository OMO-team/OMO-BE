package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class RoadmapScheduleCalculator {

    public LocalDate calculateTaskDueDate(
            LocalDate startDate,
            LocalDate departureDate,
            int daysBeforeDeparture,
            int maxDaysBeforeDeparture
    ) {
        if (departureDate == null) {
            return null;
        }
        if (startDate.isAfter(departureDate)) {
            throw new RoadmapException(RoadmapErrorCode.INVALID_DEPARTURE_DATE);
        }
        if (daysBeforeDeparture < 0 || daysBeforeDeparture > maxDaysBeforeDeparture) {
            throw new RoadmapException(RoadmapErrorCode.INVALID_TASK_TEMPLATE_SCHEDULE);
        }
        if (maxDaysBeforeDeparture == 0) {
            return departureDate.minusDays(daysBeforeDeparture);
        }

        long preparationDays = ChronoUnit.DAYS.between(startDate, departureDate);
        if (preparationDays >= maxDaysBeforeDeparture) {
            return departureDate.minusDays(daysBeforeDeparture);
        }

        long compressedDaysBeforeDeparture = Math.round(
                (double) daysBeforeDeparture * preparationDays / maxDaysBeforeDeparture
        );
        return departureDate.minusDays(compressedDaysBeforeDeparture);
    }

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
