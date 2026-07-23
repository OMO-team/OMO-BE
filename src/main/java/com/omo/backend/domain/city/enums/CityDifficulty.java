package com.omo.backend.domain.city.enums;

import com.omo.backend.domain.city.exception.CityErrorCode;
import com.omo.backend.domain.city.exception.CityException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Locale;

@Getter
@AllArgsConstructor
public enum CityDifficulty {
        EASY(new BigDecimal("4.0")),
        NORMAL(new BigDecimal("3.0")),
        HARD(new BigDecimal("2.0"));

        private final BigDecimal minScore;

        public static CityDifficulty from(String value){
                try{
                        return valueOf(value.toUpperCase(Locale.ROOT));
                }catch (IllegalArgumentException e){
                        throw new CityException(CityErrorCode.INVALID_DIFFICULTY);
                }
        }
}

