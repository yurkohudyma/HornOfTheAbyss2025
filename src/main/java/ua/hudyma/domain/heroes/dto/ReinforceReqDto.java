package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.creatures.Creature;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.Map;

public record ReinforceReqDto(
        String heroCode,
        Map<CreatureType, Integer> armyMap
) {}
