package com.omo.backend.domain.report.repository;

import com.omo.backend.domain.report.entity.CityReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityReviewRepository extends JpaRepository<CityReview, Long> {
    List<CityReview> findByCityIdAndDeletedAtIsNull(Long cityId);
}