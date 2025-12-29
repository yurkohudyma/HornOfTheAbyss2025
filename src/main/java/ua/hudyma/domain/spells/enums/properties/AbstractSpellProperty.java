package ua.hudyma.domain.spells.enums.properties;

import ua.hudyma.domain.heroes.enums.PrimarySkill;

import java.util.Map;
import java.util.Set;

public interface AbstractSpellProperty {
    String getName();
    Map<PrimarySkill, Integer> getSkillModifierMap();
    Set<String> getTargetCreatureSet();
}
