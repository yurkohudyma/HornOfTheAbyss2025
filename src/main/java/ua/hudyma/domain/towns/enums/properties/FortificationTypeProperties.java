package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.towns.enums.AbstractBuildingType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.GOLD;
import static ua.hudyma.resource.enums.ResourceType.WOOD;

@Getter
@RequiredArgsConstructor
public enum FortificationTypeProperties implements AbstractBuildingTypeProperties{
    FORT (Map.of(),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 20,
                            GOLD, 5000))
            ),
    CITADEL(Map.of(),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 20,
                            GOLD, 5000))
    ),
    CASTLE(Map.of(),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 20,
                            GOLD, 5000))
    );

    //todo fix data

    private final Map<AbstractBuildingType, Integer> requiredBuildingMap;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll((resources));
        return map;
    }
    private static Map<AbstractBuildingType, Integer> toMap(
            Map<? extends AbstractBuildingType, Integer> source) {
        return new HashMap<>(source);
    }
}
