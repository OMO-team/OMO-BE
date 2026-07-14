package com.omo.backend.domain.country.service;

import com.omo.backend.domain.country.repository.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CountryCommandService {

    private final CountryRepository countryRepository;
}
