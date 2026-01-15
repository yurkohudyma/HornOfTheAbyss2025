package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.heroes.enums.SkillLevel;

import java.util.EnumMap;

public record HeroSkillSpellModifierDto(
        PrimarySkill skill,
        Integer skillCoefficient,
        EnumMap<SkillLevel, Integer> skillLevelModifierMap
) {
}
