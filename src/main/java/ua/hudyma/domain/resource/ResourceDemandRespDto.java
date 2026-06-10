package ua.hudyma.domain.resource;

import ua.hudyma.domain.resource.enums.ResourceType;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.enums.Faction;

import java.util.EnumMap;
import java.util.EnumSet;

public record ResourceDemandRespDto(
        EnumMap<CommonBuildingType, Integer> requiredBuiltBuildings,
        EnumMap<ResourceType, Integer> requiredResourceMap,
        EnumSet<Faction>excludedFactions
) {
}
