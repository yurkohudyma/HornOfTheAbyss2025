package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.heroes.enums.HeroSpecialtyType;
import ua.hudyma.domain.heroes.enums.SkillLevel;

public record CalcSpecialtyReq(
        Long heroLevel,
        HeroSpecialtyType heroSpecialtyType,
        String specialtyProperty //spell, secondary skill or warmachine type
) {

}
