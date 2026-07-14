package com.omo.backend.domain.report.repository;

import com.omo.backend.domain.report.entity.CityRelatedResource;
import com.omo.backend.domain.report.enums.ResourceTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CityRelatedResourceRepository extends JpaRepository<CityRelatedResource, Long> {
    List<CityRelatedResource> findByCityIdAndDeletedAtIsNull(Long cityId);
    List<CityRelatedResource> findByCityIdAndTopicAndDeletedAtIsNull(Long cityId, ResourceTopic topic);
}
