package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.enums.Faction;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record TownReqDto(
        Long ownerId,
        String name,
        Faction faction,
        Map<String, Integer> dwellingMap,
        List<CreatureSlot> garrisonArmy,
        Set<CommonBuildingType> commonBuildingSet
) {
}
