package ua.hudyma.domain.creatures.dto;

import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.enums.CreatureProperty;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.enums.Faction;

import java.util.List;
import java.util.Map;

public record CreatureRespDto(
        Long id,
        Faction faction,
        CreatureType creatureType,
        Map<CreatureProperty, List<CreaturePropertyValue>>
                creaturePropertyMap,
        Map<CreatureSkill, List<CreatureSkillValue>>
                creatureSkillMap
) {
}
