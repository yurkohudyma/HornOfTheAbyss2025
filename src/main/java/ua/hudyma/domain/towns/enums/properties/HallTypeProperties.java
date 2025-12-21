package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.*;
import static ua.hudyma.domain.towns.enums.FortificationType.CASTLE;
import static ua.hudyma.resource.enums.ResourceType.GOLD;

@Getter
@RequiredArgsConstructor
public enum HallTypeProperties implements AbstractBuildingTypeProperties{
    VILLAGE_HALL (Map.of(),
            emptyMap(ResourceType.class)),
    TOWN_HALL (Map.of(TAVERN.name(), 0),
            toEnumMap(
                    Map.of(GOLD, 2500))),
    CITY_HALL (Map.of(
            TOWN_HALL.name(), 0,
            BLACKSMITH.name(), 0,
            MAGE_GUILD.name(), 1,
            MARKETPLACE.name(), 0),
            toEnumMap(
                    Map.of(GOLD, 5000))),
    CAPITOL (Map.of(
            CITY_HALL.name(), 0,
            CASTLE.name(), 0),
            toEnumMap(
                    Map.of(GOLD, 10000)));

    private final Map<String, Integer> requiredBuildingMap;
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
