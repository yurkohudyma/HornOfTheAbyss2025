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

import static ua.hudyma.domain.creatures.enums.creaturetypes.RampartEssentialCreatureType.CENTAUR;
import static ua.hudyma.domain.towns.enums.CommonBuildingType.MAGE_GUILD;
import static ua.hudyma.domain.towns.enums.dwelling.RampartDwellingType.DWARF_COTTAGE;
import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum RampartDwellingTypeProperties implements AbstractDwellingTypeProperties {
    CENTAUR_STABLES(toStringMap(
            Map.of(FortificationType.FORT.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 10,
                            GOLD, 500))),
    UPG_CENTAUR_STABLES(toStringMap(
            Map.of(CENTAUR_STABLES.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 5,
                            GOLD, 1000))),
    DWARF_COTTAGE(toStringMap(
            Map.of()),
            toResourceEnumMap(
                    Map.of(WOOD, 5,
                            GOLD, 1000))),
    UPG_DWARF_COTTAGE(toStringMap(
            Map.of(DWARF_COTTAGE.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 5,
                            GOLD, 1500))),
    HOMESTEAD(toStringMap(
            Map.of(CENTAUR_STABLES.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 10,
                            GOLD, 1500))),
    UPG_HOMESTEAD(toStringMap(
            Map.of(HOMESTEAD.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 10,
                            GOLD, 1000))),
    ENCHANTED_SPRING(toStringMap(
            Map.of(HOMESTEAD.name(), 0)),
            toResourceEnumMap(
                    Map.of(CRYSTAL, 10,
                            GOLD, 2000))),
    UPG_ENCHANTED_SPRING(toStringMap(
            Map.of(ENCHANTED_SPRING.name(), 0)),
            toResourceEnumMap(
                    Map.of(CRYSTAL, 5,
                            GOLD, 2000))),
    DENDROID_ARCHES(toStringMap(
            Map.of()),
            toResourceEnumMap(
                    Map.of(GOLD, 2500))),
    UPG_DENDROID_ARCHES(toStringMap(
            Map.of(DENDROID_ARCHES.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 0,
                            GOLD, 1500))),
    UNICORN_GLADE(toStringMap(
            Map.of(ENCHANTED_SPRING.name(), 0,
                    DENDROID_ARCHES.name(), 0)),
            toResourceEnumMap(
                    Map.of(WOOD, 5,
                            ORE, 5,
                            GEMS, 10,
                            GOLD, 4000))),
    UPG_UNICORN_GLADE(toStringMap(
            Map.of(UNICORN_GLADE.name(), 0)),
            toResourceEnumMap(
                    Map.of(
                            GEMS, 5,
                            GOLD, 3000))),
    DRAGON_CLIFFS(toStringMap(
            Map.of(MAGE_GUILD.name(), 2,
                    UNICORN_GLADE.name(),0)),
            toResourceEnumMap(
                    Map.of(ORE, 30,
                            CRYSTAL, 20,
                            GOLD, 10000))),
    UPG_DRAGON_CLIFFS(toStringMap(
            Map.of(MAGE_GUILD.name(), 3,
                    DRAGON_CLIFFS.name(), 0)),
            toResourceEnumMap(
                    Map.of(ORE, 30,
                            CRYSTAL, 20,
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
    private static Map<String, Integer> toStringMap(Map<String, Integer> source) {
        return new HashMap<>(source);
    }
}
