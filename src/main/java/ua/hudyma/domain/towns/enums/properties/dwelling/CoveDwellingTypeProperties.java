package ua.hudyma.domain.towns.enums.properties.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.FortificationType;
import ua.hudyma.domain.towns.enums.UniqueBuildingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.dwelling.CastleDwellingType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum CoveDwellingTypeProperties implements AbstractDwellingTypeProperties {
    //todo correct values to match HOTA
    WATERFALL(toStringMap(
            Map.of(FortificationType.FORT.name(), 0)),
            toResourceEnumMap(
                    Map.of(ORE, 5,
                            GOLD, 500))),
    SHACK(toStringMap(
            Map.of(CastleDwellingType.GUARDHOUSE.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            GOLD, 500))),
    WAREHOUSE(toStringMap(
            Map.of(CastleDwellingType.BARRACKS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            GOLD, 1000))),
    NEST(toStringMap(Map.of(
            CommonBuildingType.BLACKSMITH.name(), 0,
            CastleDwellingType.GUARDHOUSE.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            GOLD, 2000))),
    TOWER_OF_THE_SEAS(toStringMap(Map.of(
            CommonBuildingType.MAGE_GUILD.name(), 1,
            CastleDwellingType.BARRACKS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 2,
                            CRYSTAL, 2,
                            SULFUR, 2,
                            GEMS, 2,
                            GOLD, 3000))),
    NIX_PORT(toStringMap(Map.of(
            UniqueBuildingType.STABLES.name(), 0,
            CastleDwellingType.BARRACKS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 20,
                            GOLD, 5000))),
    MAELSTROM(toStringMap(
            Map.of(CastleDwellingType.MONASTERY.getCode(), 0)),
            toResourceEnumMap(
                    Map.of(
                            MERCURY, 10,
                            CRYSTAL, 10,
                            SULFUR, 10,
                            GEMS, 10,
                            GOLD, 20000)));
    private final Map<String, Integer> requiredBuildingMap;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll((resources));
        return map;
    }
    private static <T extends Enum<T>> Map<? extends Enum<T>, Integer> toMap(
            Map<? extends Enum<T>, Integer> source) {
        return new HashMap<>(source);
    }
    private static Map<String, Integer> toStringMap (Map<String, Integer> source){
        return new HashMap<>(source);
    }
}
