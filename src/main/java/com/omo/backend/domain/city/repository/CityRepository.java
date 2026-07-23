package com.omo.backend.domain.city.repository;
import com.omo.backend.domain.city.entity.City;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {

    @EntityGraph(attributePaths = "country")
    List<City> findAllWithCountryByCityIdIn(List<Long> cityIds);

    @Override
    @EntityGraph(attributePaths = "country")
    List<City> findAll(Specification<City> spec);
}
