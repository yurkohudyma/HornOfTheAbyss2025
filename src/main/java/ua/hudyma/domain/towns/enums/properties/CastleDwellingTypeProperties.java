package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum CastleDwellingTypeProperties implements AbstractBuildingTypeProperties {
    GUARDHOUSE (toEnumMap(FortificationType.class,
            Map.of(FortificationType.FORT, 0)),
            toEnumMap(ResourceType.class,
                    Map.of(ORE, 5,
                            GOLD, 500))),
    ARCHER_TOWER (toEnumMap(CastleDwellingType.class,
            Map.of(CastleDwellingType.GUARDHOUSE, 0)),
            toEnumMap(ResourceType.class,
                    Map.of(
                    WOOD, 5,
                    ORE, 5,
                    GOLD, 500))),
    GRIFFIN_TOWER (toEnumMap(CastleDwellingType.class,
            Map.of(CastleDwellingType.BARRACKS, 0)),
            toEnumMap(ResourceType.class,
                    Map.of(
                    ORE, 5,
                    GOLD, 1000))),
    BARRACKS (toEnumMap(CommonBuildingType.class,
            Map.of(CommonBuildingType.BLACKSMITH, 0)), //todo include CastleDwellingType.GuardHouse
            toEnumMap(ResourceType.class,
                    Map.of(
                            ORE, 5,
                            GOLD, 2000))),
    MONASTERY (toEnumMap(CommonBuildingType.class,
            Map.of(CommonBuildingType.MAGE_GUILD, 1)), //todo include CastleDwellingType.Barracks
            toEnumMap(ResourceType.class,
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 2,
                            CRYSTAL, 2,
                            SULFUR, 2,
                            GEMS, 2,
                            GOLD, 3000))),
    TRAINING_GROUNDS (toEnumMap(UniqueBuildingType.class,
            Map.of(UniqueBuildingType.STABLES, 0)), //todo include CastleDwellingType.Barracks
            toEnumMap(ResourceType.class,
                    Map.of(
                            WOOD, 20,
                            GOLD, 5000))),
    PORTAL_OF_GLORY (toEnumMap(CastleDwellingType.class,
            Map.of(CastleDwellingType.MONASTERY, 0)),
            toEnumMap(ResourceType.class,
                    Map.of(
                            MERCURY, 10,
                            CRYSTAL, 10,
                            SULFUR, 2,
                            GEMS, 2,
                            GOLD, 20000)));

    private final EnumMap<? extends AbstractBuildingType, Integer> requiredBuiltBuildings;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private static <T extends Enum<T>> EnumMap<T, Integer> toEnumMap(
            Class<T> enumClass, Map<T, Integer> resources) {
        var map = new EnumMap<T, Integer>(enumClass);
        map.putAll(resources);
        return map;
    }
}
