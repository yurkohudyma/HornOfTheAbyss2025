package ua.hudyma.domain.towns.converter;

import org.reflections.Reflections;
import ua.hudyma.domain.towns.enums.AbstractBuildingType;
import ua.hudyma.domain.towns.enums.UniqueBuildingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.enums.Faction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BuildingTypeResolver {

    private static final Reflections reflections;
    public static final Set<Class<? extends AbstractBuildingType>> ENUM_TYPES;
    public static final Set<Class<? extends AbstractDwellingType>> ENUM_DWELL_TYPES;

    static {
        reflections = new Reflections("ua.hudyma.domain.towns.enums");
        ENUM_TYPES = reflections.getSubTypesOf(AbstractBuildingType.class);
        ENUM_DWELL_TYPES = reflections.getSubTypesOf(AbstractDwellingType.class);
    }

    public static AbstractBuildingType resolve(String value) {
        for (Class<? extends AbstractBuildingType> type : ENUM_TYPES) {
            try {
                return (AbstractBuildingType) Enum.valueOf(
                        (Class) type, value);
            } catch (IllegalArgumentException ignored) {}
        }
        throw new IllegalArgumentException("BuildingTypeResolver: unknown building type: " + value);
    }

    public static AbstractDwellingType resolveDwellingType (String value) {
        for (Class<? extends AbstractDwellingType> type : ENUM_DWELL_TYPES) {
            try {
                return (AbstractDwellingType) Enum.valueOf(
                        (Class) type, value);
            } catch (IllegalArgumentException ignored) {}
        }
        throw new IllegalArgumentException("BuildingTypeResolver : unknown dwelling type: " + value);
    }

    public static Set<String> retrieveAllTownSpecificUniqueBuildings(Faction townFaction) {
        return Arrays
                .stream(UniqueBuildingType.values())
                .filter(type -> type.getFaction().equals(townFaction))
                .map(String::valueOf)
                .collect(Collectors.toSet());
    }

    public static Map<String, Integer> retrieveAllTownSpecificUpgradedDwellings(Faction townFaction) {
        for (Class<? extends AbstractDwellingType> type : ENUM_DWELL_TYPES) {
            if (type.getSimpleName().toUpperCase().contains(townFaction.name())) {
                return Arrays
                        .stream(type.getEnumConstants())
                        .map(String::valueOf)
                        .filter(constant -> constant.startsWith("UPG_"))
                        .collect(Collectors.toMap(
                                Function.identity(),
                                k -> 0));
            }
        }
        return Map.of();
    }
}
