package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.DwellingType;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record TownReqDto(
        Long ownerId,
        String name,
        Faction faction,
        List<DwellingType> dwellingTypeList,
        List<CreatureSlot> garrisonArmy,
        Set<CommonBuildingType> commonBuildingSet
) {
}
