package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.enums.Faction;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.*;
import static ua.hudyma.enums.Faction.*;
import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum CommonBuildingTypeProperties implements AbstractBuildingTypeProperties {
    BLACKSMITH(emptyMap(),
    toEnumMap(ResourceType.class,
            Map.of(
                    WOOD, 5,
                    GOLD, 1000)),
            emptyFactions()),
    MAGE_GUILD_L1(emptyMap(),
            toEnumMap(ResourceType.class,
            Map.of(
                    WOOD, 5,
                    ORE, 5,
                    GOLD, 2000)),
            emptyFactions()),
    MAGE_GUILD_L2(toEnumMap(CommonBuildingType.class,
            Map.of(MAGE_GUILD, 1)),
            toEnumMap(ResourceType.class,
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 4,
                            CRYSTAL, 4,
                            SULFUR, 4,
                            GEMS, 4,
                            GOLD, 1000)),
            emptyFactions()),
    MAGE_GUILD_L3(toEnumMap(CommonBuildingType.class,
            Map.of(MAGE_GUILD, 2)),
            toEnumMap(ResourceType.class,
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 6,
                            CRYSTAL, 6,
                            SULFUR, 6,
                            GEMS, 6,
                            GOLD, 1000)),
            emptyFactions()),
    MAGE_GUILD_L4(toEnumMap(CommonBuildingType.class,
            Map.of(MAGE_GUILD, 3)),
            toEnumMap(ResourceType.class,
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 8,
                            CRYSTAL, 8,
                            SULFUR, 8,
                            GEMS, 8,
                            GOLD, 1000)),
            EnumSet.of(
                    STRONGHOLD, FORTRESS)),
    MAGE_GUILD_L5(toEnumMap(CommonBuildingType.class,
            Map.of(MAGE_GUILD, 4)),
            toEnumMap(ResourceType.class,
                    Map.of(
                    WOOD, 5,
                    ORE, 5,
                    MERCURY, 10,
                    CRYSTAL, 10,
                    SULFUR, 10,
                    GEMS, 10,
                    GOLD, 1000)),
            EnumSet.of(
            STRONGHOLD, FORTRESS, COVE, CASTLE)),
    MARKETPLACE(emptyMap(),
            toEnumMap(ResourceType.class,
                    Map.of(
                            WOOD, 5,
                            GOLD, 500)),
            emptyFactions()),
    RESOURCE_SILO(toEnumMap(CommonBuildingType.class,
            Map.of(CommonBuildingType.MARKETPLACE, 0)),
            toEnumMap(ResourceType.class,
                    Map.of(
                    ORE, 5,
                    GOLD, 5000)),
            emptyFactions()),
    TAVERN(emptyMap(),
            toEnumMap(ResourceType.class,
                    Map.of(
                    WOOD, 5,
                    GOLD, 500)),
            emptyFactions()),
    SHIPYARD(emptyMap(),
            toEnumMap(ResourceType.class,
                    Map.of(
                    WOOD, 20,
                    GOLD, 2000)),
            emptyFactions());
    private static <T extends Enum<T>> EnumMap<T, Integer> emptyMap() {
        return new EnumMap<>((Class<T>) CommonBuildingType.class);
    }
    private final EnumMap<CommonBuildingType, Integer> requiredBuiltBuildings;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private final EnumSet<Faction> excludedFactions;
    private static EnumSet<Faction> emptyFactions() {
        return EnumSet.noneOf(Faction.class);
    }
    private static <T extends Enum<T>> EnumMap<T, Integer> toEnumMap(
            Class<T> enumClass, Map<T, Integer> resources) {
        var map = new EnumMap<T, Integer>(enumClass);
        map.putAll(resources);
        return map;
    }
}
