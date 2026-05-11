package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.heroes.enums.HeroSpecialtyType;

public record CalcSpecialtyReq(
        Long heroLevel,
        HeroSpecialtyType heroSpecialtyType,
        String specialtyProperty
) {

}
