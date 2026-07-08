package com.omo.backend.domain.member.repository;

import com.omo.backend.domain.member.entity.MemberSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberSettingsRepository extends JpaRepository<MemberSettings, Long> {
}
