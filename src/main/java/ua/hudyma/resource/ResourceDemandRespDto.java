package ua.hudyma.resource;

import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.enums.Faction;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.EnumSet;

public record ResourceDemandRespDto(
        EnumMap<CommonBuildingType, Integer> requiredBuiltBuildings,
        EnumMap<ResourceType, Integer> requiredResourceMap,
        EnumSet<Faction>excludedFactions
) {
}
