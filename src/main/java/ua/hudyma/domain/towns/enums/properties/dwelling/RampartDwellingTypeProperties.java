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
public enum RampartDwellingTypeProperties implements AbstractDwellingTypeProperties {
    DWARF_COTTAGE(toStringMap(
            Map.of(FortificationType.FORT.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 5,
                            GOLD, 1000)));

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
