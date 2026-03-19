package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum SpellReplaceDemands {
    ONE(toEnumMap(Map.of(
            GOLD, 1000,
            GEMS, 2,
            MERCURY, 2,
            SULFUR, 2,
            CRYSTAL, 2))),
    TWO(toEnumMap(Map.of(
            GOLD, 1000,
            GEMS, 4,
            MERCURY, 4,
            SULFUR, 4,
            CRYSTAL, 4))),
    THREE(toEnumMap(Map.of(
            GOLD, 1000,
            GEMS, 6,
            MERCURY, 6,
            SULFUR, 6,
            CRYSTAL, 6))),
    FOUR(toEnumMap(Map.of(
            GOLD, 1000,
            GEMS, 8,
            MERCURY, 8,
            SULFUR, 8,
            CRYSTAL, 8))),
    FIVE(toEnumMap(Map.of(
            GOLD, 1000,
            GEMS, 10,
            MERCURY, 10,
            SULFUR, 10,
            CRYSTAL, 10)));

    private static EnumMap<ResourceType, Integer> toEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll(resources);
        return map;
    }
    private final EnumMap<ResourceType, Integer> resourceMap;
}
