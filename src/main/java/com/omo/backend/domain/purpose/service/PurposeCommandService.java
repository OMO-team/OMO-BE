package com.omo.backend.domain.purpose.service;

import com.omo.backend.domain.purpose.repository.PurposeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PurposeCommandService {
    private final PurposeRepository purposeRepository;
}
