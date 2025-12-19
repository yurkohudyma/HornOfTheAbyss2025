package ua.hudyma.domain.towns.converter;

import org.reflections.Reflections;
import ua.hudyma.domain.towns.enums.AbstractBuildingType;

import java.util.Set;

public final class BuildingTypeResolver {

    private static final Reflections reflections;
    public static final Set<Class<? extends AbstractBuildingType>> ENUM_TYPES;

    static {
        reflections = new Reflections("ua.hudyma.domain.towns.enums");
        ENUM_TYPES = reflections.getSubTypesOf(AbstractBuildingType.class);
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
}
