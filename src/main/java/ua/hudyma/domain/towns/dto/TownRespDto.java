package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record TownRespDto(
        String ownerName,
        String name,
        Alignment alignment,
        Faction faction,
        String visitingHero,
        String garrisonHero,
        List<DwellingType> dwellingTypeList,
        List<CreatureSlot> garrisonArmy,
        List<String> commonBuildingList,
        List<HordeBuildingType> hordeBuildingList,
        List<UniqueBuildingType> uniqueBuildingList,
        GrailBuildingType grailBuilding,
        List<Enum<?>> initialConstantList)
{}
