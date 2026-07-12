package com.omo.backend.domain.document.repository;

import com.omo.backend.domain.document.entity.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentTemplateRepository
        extends JpaRepository<DocumentTemplate, Long> {
}
