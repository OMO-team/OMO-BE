package com.omo.backend.domain.city.service;

import com.omo.backend.domain.city.repository.CityRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CityCommandService {

    private final CityRepository cityRepository;
}
