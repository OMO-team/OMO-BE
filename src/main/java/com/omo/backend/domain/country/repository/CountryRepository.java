package com.omo.backend.domain.country.repository;
import com.omo.backend.domain.country.entity.Country;
import com.omo.backend.domain.purpose.enums.PurposeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    // 선택한 목적에 해당하는 도시가 있는 국가만 반환
    @Query("""
            select distinct city.country
            from City city
            join city.cityPurposes cp
            join cp.purpose p
            where p.type = :purposeType
            order by city.country.name asc
""")
    List<Country> findCountriesByPurposeType(@Param("purposeType")PurposeEnum purposeType);
}
