package com.omo.backend.domain.report.repository;

import com.omo.backend.domain.report.entity.CityCoreSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CityCoreSummaryRepository extends JpaRepository<CityCoreSummary, Long> {
    List<CityCoreSummary> findByCityIdAndDeletedAtIsNull(Long cityId);
}
