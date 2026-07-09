package com.omo.backend.domain.terms.repository;

import com.omo.backend.domain.member.entity.MemberTerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTermsRepository extends JpaRepository<MemberTerms, Long> {
}
