package com.omo.backend.domain.terms.repository;

import com.omo.backend.domain.terms.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

    List<Terms> findAllByRequiredTrueAndDeletedAtIsNull();

    List<Terms> findAllByDeletedAtIsNullOrderByIdAsc();
}
