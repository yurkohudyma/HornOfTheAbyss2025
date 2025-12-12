package ua.hudyma.mapper;

import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.DwellingType;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;

import java.util.List;

public record TownRespDto(
        String ownerName,
        String name,
        Alignment alignment,
        Faction faction,
        List<DwellingType> dwellingTypeList,
        List<CreatureSlot> garrisonArmy,
        List<CommonBuildingType> commonBuildingList,
        List<Enum<?>> initialConstantList) {}
