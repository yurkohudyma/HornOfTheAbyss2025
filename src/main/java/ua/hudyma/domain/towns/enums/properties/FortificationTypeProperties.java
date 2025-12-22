package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.towns.enums.FortificationType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum FortificationTypeProperties implements AbstractBuildingTypeProperties{
    FORT (EnumSet.noneOf(FortificationType.class),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 20,
                            ORE, 20,
                            GOLD, 5000))
            ),
    CITADEL(EnumSet.of(FortificationType.FORT),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            GOLD, 2500))
    ),
    CASTLE(EnumSet.of(FortificationType.CITADEL),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 10,
                            ORE, 10,
                            GOLD, 5000)));

    private final EnumSet<FortificationType> requiredBuildingSet;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll((resources));
        return map;
    }
}
