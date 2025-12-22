package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.SHIPYARD;
import static ua.hudyma.domain.towns.enums.CommonBuildingType.TAVERN;
import static ua.hudyma.domain.towns.enums.dwelling.CastleDwellingType.BARRACKS;
import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum UniqueBuildingTypeProperties implements AbstractBuildingTypeProperties {

    //castle
    BROTHERHOOD_OF_THE_SWORD(
            Set.of(TAVERN.name()),
            toEnumMap(
                    toEnumMap(
                            Map.of(
                                    WOOD, 5,
                                    GOLD, 500)))),
    LIGHTHOUSE (Set.of(SHIPYARD.name()),
            toEnumMap(
            Map.of(
                    ORE, 10,
                    GOLD, 2000))),
    STABLES (Set.of(BARRACKS.name()),
            toEnumMap(
            Map.of(
                    ORE, 10,
                    GOLD, 2000)));

    //inferno
    //CASTLE_GATE (INFERNO),
    //ORDER_OF_FIRE(INFERNO);
    private final Set<String> requiredBuildingSet;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private static <T extends Enum<T>> EnumMap<T, Integer> toEnumMap(
            Map<T, Integer> resources) {
        var map = new EnumMap<T, Integer>((Class<T>) ResourceType.class);
        map.putAll(resources);
        return map;
    }

    private static <T extends Enum<T>> EnumMap<T, Integer> emptyMap(Class<T> enumClass) {
        return new EnumMap<>(enumClass);
    }
}
