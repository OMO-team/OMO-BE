package com.omo.backend.domain.report.repository;

import com.omo.backend.domain.report.entity.CityProsCons;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CityProsConsRepository extends JpaRepository<CityProsCons, Long> {
    List<CityProsCons> findByCityIdAndDeletedAtIsNullOrderByDisplayOrderAsc(Long cityId);
}
