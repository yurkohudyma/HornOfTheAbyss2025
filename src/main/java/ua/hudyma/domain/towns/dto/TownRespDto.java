package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;

import java.util.List;
import java.util.Map;

public record TownRespDto(
        String ownerName,
        String name,
        Alignment alignment,
        Faction faction,
        String visitingHero,
        String garrisonHero,
        Map<String, Integer> dwellingMap,
        List<CreatureSlot> garrisonArmy,
        Map<CommonBuildingType, Integer> commonBuildingMap,
        HallType hallType)
        //,List<HordeBuildingType> hordeBuildingList,
        //List<UniqueBuildingType> uniqueBuildingList
        //, GrailBuildingType grailBuilding
{}
