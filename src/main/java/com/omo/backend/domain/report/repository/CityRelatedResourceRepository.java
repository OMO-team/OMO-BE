package com.omo.backend.domain.report.repository;

import com.omo.backend.domain.report.entity.CityRelatedResource;
import com.omo.backend.domain.report.enums.ResourceTopic;
import com.omo.backend.domain.report.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CityRelatedResourceRepository extends JpaRepository<CityRelatedResource, Long> {
    List<CityRelatedResource> findByCityIdAndDeletedAtIsNull(Long cityId);
    List<CityRelatedResource> findByCityIdAndTopicAndDeletedAtIsNull(Long cityId, ResourceTopic topic);
    List<CityRelatedResource> findByCityIdAndResourceTypeAndDeletedAtIsNull(Long cityId, ResourceType resourceType);
    List<CityRelatedResource> findByCityIdAndTopicAndResourceTypeAndDeletedAtIsNull(
            Long cityId, ResourceTopic topic, ResourceType resourceType);
}
