package ua.hudyma.dto;

import ua.hudyma.domain.creatures.CreatureType;

import java.util.Map;

public record TownHireCreaturesReqDto(
        String townName,
        String heroId,
        Map<CreatureType, Integer> reqMap
) {
}
