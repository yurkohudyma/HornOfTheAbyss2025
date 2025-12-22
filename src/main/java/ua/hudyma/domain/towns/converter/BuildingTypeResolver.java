package ua.hudyma.domain.towns.converter;

import org.reflections.Reflections;
import ua.hudyma.domain.towns.enums.AbstractBuildingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;

import java.util.Set;

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
        throw new IllegalArgumentException("Unknown building type: " + value);
    }

    public static AbstractDwellingType resolveDwellingType (String value) {
        for (Class<? extends AbstractDwellingType> type : ENUM_DWELL_TYPES) {
            try {
                return (AbstractDwellingType) Enum.valueOf(
                        (Class) type, value);
            } catch (IllegalArgumentException ignored) {}
        }
        throw new IllegalArgumentException("Unknown building type: " + value);
    }
}
