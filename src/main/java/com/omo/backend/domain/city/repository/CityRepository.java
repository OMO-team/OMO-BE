package com.omo.backend.domain.city.repository;
import com.omo.backend.domain.city.entity.City;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {

    @EntityGraph(attributePaths = "country")
    List<City> findAllWithCountryByCityIdIn(List<Long> cityIds);

    @Override
    @EntityGraph(attributePaths = "country")
    List<City> findAll(Specification<City> spec);

    @EntityGraph(attributePaths = "country")
    @Query("SELECT c FROM City c WHERE " +
            "(:maxBudget IS NULL OR c.monthlyCost <= :maxBudget) AND " +
            "(:highSafety IS NULL OR :highSafety = false OR c.safetyScore >= 4.0) AND " +
            "(:englishOnly IS NULL OR :englishOnly = false OR c.languageScore >= 4.0) AND " +
            "(:easyVisa IS NULL OR :easyVisa = false OR c.visaScore >= 4.0) AND " +
            "(:goodHousing IS NULL OR :goodHousing = false OR c.housingScore >= 4.0) AND " +
            "(:goodInfra IS NULL OR :goodInfra = false OR c.infraScore >= 4.0) AND " +
            "(:country IS NULL OR LOWER(c.country.name) = LOWER(:country))")
    List<City> findCandidatesByConditions(
            @Param("maxBudget") Integer maxBudget,
            @Param("highSafety") Boolean highSafety,
            @Param("englishOnly") Boolean englishOnly,
            @Param("easyVisa") Boolean easyVisa,
            @Param("goodHousing") Boolean goodHousing,
            @Param("goodInfra") Boolean goodInfra,
            @Param("country") String country
    );
}
