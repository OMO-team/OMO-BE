package com.omo.backend.domain.city.enums;

import com.omo.backend.domain.city.exception.CityErrorCode;
import com.omo.backend.domain.city.exception.CityException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@Getter
@AllArgsConstructor
public enum CityStayDuration {
    SHORT("3개월 이하"),
    MEDIUM("3-6개월"),
    LONG("6개월-1년"),
    VERY_LONG("1년 이상");

    private final String description;

    public static CityStayDuration from(String value){
        try{
            return valueOf(value.toUpperCase(Locale.ROOT));
        }catch (IllegalArgumentException e){
            throw new CityException(CityErrorCode.INVALID_STAY_DURATION);
        }
    }
}
