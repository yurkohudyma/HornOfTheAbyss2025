package ua.hudyma.domain.towns.enums.properties.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.FortificationType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.dwelling.CoveDwellingType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.BLACKSMITH;
import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum CoveDwellingTypeProperties implements AbstractDwellingTypeProperties {
    WATERFALL(toStringMap(
            Map.of(FortificationType.FORT.name(), 0)),
            toResourceEnumMap(
                    Map.of(ORE, 5,
                            WOOD, 5,
                            GOLD, 300))),
    SHACK(toStringMap(
            Map.of(CoveDwellingType.WATERFALL.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 5,
                            GOLD, 1000))),
    WAREHOUSE(toStringMap(
            Map.of(SHACK.name(), 0,
                    BLACKSMITH.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            WOOD,5,
                            CRYSTAL, 5,
                            GEMS,5,
                            SULFUR,5,
                            MERCURY,5,
                            GOLD, 3000))),
    NEST(toStringMap(Map.of(
            SHACK.name(), 0)),
            toResourceEnumMap(
                    Map.of(CRYSTAL, 2,
                            GEMS,2,
                            ORE, 5,
                            GOLD, 2000))),
    TOWER_OF_THE_SEAS(toStringMap(Map.of(
            CommonBuildingType.MAGE_GUILD.name(), 1,
            SHACK.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            CRYSTAL, 5,
                            SULFUR, 5,
                            GOLD, 2000))),
    NIX_FORT(toStringMap(Map.of(
            NEST.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 10,
                            SULFUR, 5,
                            GOLD, 3000))),
    MAELSTROM(toStringMap(
            Map.of(TOWER_OF_THE_SEAS.name(), 0,
                    NIX_FORT.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 15,
                            CRYSTAL, 15,
                            SULFUR, 20,
                            GOLD, 15000)));
    private final Map<String, Integer> requiredBuildingMap;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll((resources));
        return map;
    }
    private static Map<String, Integer> toStringMap (
            Map<String, Integer> source){
        return new HashMap<>(source);
    }
}
