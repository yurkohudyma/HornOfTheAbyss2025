package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.heroes.enums.PrimarySkill;

public record HeroExperienceRespDto(
        String heroName,
        Integer currentLevel,
        Integer nextLevel,
        Integer currentExperience,
        Integer gainedExperience,
        PrimarySkill gainedprimarySkill,
        Integer currentPrimarySkillValue,
        Integer gainedPrimarySkillValue
) {
}
