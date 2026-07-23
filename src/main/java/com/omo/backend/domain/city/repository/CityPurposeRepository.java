package com.omo.backend.domain.city.repository;

import com.omo.backend.domain.city.entity.CityPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityPurposeRepository extends JpaRepository<CityPurpose, Long> {

    boolean existsByCityCityIdAndPurposePurposeId(Long cityId, Long purposeId);
}
