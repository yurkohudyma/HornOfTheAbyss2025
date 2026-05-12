package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.heroes.enums.HeroSpecialtyType;
import ua.hudyma.domain.heroes.enums.SkillLevel;

public record CalcSpecialtyReq(
        Long heroLevel,
        HeroSpecialtyType heroSpecialtyType,
        SkillLevel skillLevel,
        String specialtyProperty //spell name, secondary skill name or warmachine type
) {

}
