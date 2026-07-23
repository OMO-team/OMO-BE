package com.omo.backend.domain.city.enums;

import com.omo.backend.domain.city.exception.CityErrorCode;
import com.omo.backend.domain.city.exception.CityException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum CityEnum {
        EASY(new BigDecimal("4.0")),
        NORMAL(new BigDecimal("3.0")),
        HARD(new BigDecimal("2.0"));

        private final BigDecimal minScore;

        public static CityEnum from(String value){
                try{
                        return valueOf(value.toUpperCase());
                }catch (IllegalArgumentException e){
                        throw new CityException(CityErrorCode.INVALID_DIFFICULTY);
                }
        }
}
