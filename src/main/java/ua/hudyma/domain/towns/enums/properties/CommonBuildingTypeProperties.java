package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.enums.Faction;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import static ua.hudyma.enums.Faction.*;
import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum CommonBuildingTypeProperties implements AbstractBuildingTypeProperties {
    BLACKSMITH(null, toEnumMap(
            Map.of(
                    WOOD, 5,
                    GOLD, 1000)),
            null),
    MAGE_GUILD_L1(null, toEnumMap(
            Map.of(
                    WOOD, 5,
                    ORE, 5,
                    GOLD, 1000)),
            null),
    MAGE_GUILD_L2(EnumSet.of(MAGE_GUILD_L1),
            toEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 4,
                            CRYSTAL, 4,
                            SULFUR, 4,
                            GEMS, 4,
                            GOLD, 1000)),
            null),
    MAGE_GUILD_L3(EnumSet.of(MAGE_GUILD_L2),
            toEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 6,
                            CRYSTAL, 6,
                            SULFUR, 6,
                            GEMS, 6,
                            GOLD, 1000)),
            null),
    MAGE_GUILD_L4(EnumSet.of(MAGE_GUILD_L3),
            toEnumMap(
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
    MAGE_GUILD_L5(EnumSet.of(MAGE_GUILD_L4),
            null, EnumSet.of(
            STRONGHOLD, FORTRESS, COVE, CASTLE)),
    MARKETPLACE(null,
            toEnumMap(
                    Map.of(
                            WOOD, 5,
                            GOLD, 500)),
            null),
    RESOURCE_SILO(EnumSet.of(MARKETPLACE),
            toEnumMap(Map.of(
                    ORE, 5,
                    GOLD, 5000)),
            null),
    TAVERN(null,
            toEnumMap(Map.of(
                    WOOD, 5,
                    GOLD, 500)),
            null);
    private final EnumSet<CommonBuildingTypeProperties> requiredBuildingBuilt;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private final EnumSet<Faction> excludedFactions;

    private static EnumMap<ResourceType, Integer> toEnumMap
            (Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(
                ResourceType.class);
        map.putAll(resources);
        return map;
    }
}
