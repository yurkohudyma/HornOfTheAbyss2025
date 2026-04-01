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
public enum CastleDwellingTypeProperties implements AbstractDwellingTypeProperties {
    GUARDHOUSE(toStringMap(
            Map.of(FortificationType.FORT.name(), 0)),
            toResourceEnumMap(
                    Map.of(ORE, 10,
                            GOLD, 500))),
    UPG_GUARDHOUSE(toStringMap(
            Map.of(GUARDHOUSE.name(), 0)),
            toResourceEnumMap(
                    Map.of(ORE, 5,
                            GOLD, 1000))),
    ARCHER_TOWER(toStringMap(
            Map.of(GUARDHOUSE.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            GOLD, 1000))),
    UPG_ARCHER_TOWER(toStringMap(
            Map.of(ARCHER_TOWER.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            GOLD, 1000))),
    GRIFFIN_TOWER(toStringMap(
            Map.of(CastleDwellingType.BARRACKS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            GOLD, 1000))),
    UPG_GRIFFIN_TOWER(toStringMap(
            Map.of(GRIFFIN_TOWER.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            GOLD, 1000))),
    BARRACKS(toStringMap(Map.of(
            CommonBuildingType.BLACKSMITH.name(), 0,
            GUARDHOUSE.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            GOLD, 2000))),
    UPG_BARRACKS(toStringMap(
            Map.of(
            BARRACKS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            ORE, 5,
                            GOLD, 2000))),
    MONASTERY(toStringMap(Map.of(
            CommonBuildingType.MAGE_GUILD.name(), 1,
            BARRACKS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 5,
                            ORE, 5,
                            MERCURY, 2,
                            CRYSTAL, 2,
                            SULFUR, 2,
                            GEMS, 2,
                            GOLD, 3000))),
    UPG_MONASTERY(toStringMap(Map.of(
            MONASTERY.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 2,
                            ORE, 2,
                            MERCURY, 2,
                            CRYSTAL, 2,
                            SULFUR, 2,
                            GEMS, 2,
                            GOLD, 1000))),
    TRAINING_GROUNDS(toStringMap(Map.of(
            UniqueBuildingType.STABLES.name(), 0,
            BARRACKS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 20,
                            GOLD, 5000))),
    UPG_TRAINING_GROUNDS(toStringMap(
            Map.of(
            TRAINING_GROUNDS.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            WOOD, 10,
                            GOLD, 3000))),
    PORTAL_OF_GLORY(toStringMap(
            Map.of(MONASTERY.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            MERCURY, 10,
                            CRYSTAL, 10,
                            SULFUR, 10,
                            GEMS, 10,
                            GOLD, 20000))),
    UPG_PORTAL_OF_GLORY(toStringMap(
            Map.of(PORTAL_OF_GLORY.name(), 0)),
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
