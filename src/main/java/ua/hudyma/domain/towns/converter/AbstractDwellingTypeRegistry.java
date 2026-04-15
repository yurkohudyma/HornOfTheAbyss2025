package ua.hudyma.domain.towns.converter;

import org.reflections.Reflections;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;

import java.util.Set;

public class AbstractDwellingTypeRegistry {
    private static final Reflections reflections;
    private static final Set<Class<? extends AbstractDwellingType>> ENUM_TYPES;

    private AbstractDwellingTypeRegistry() {
    }

    static {
        reflections = new Reflections("ua.hudyma.domain.towns.enums.dwelling");
        ENUM_TYPES = reflections.getSubTypesOf(AbstractDwellingType.class);
    }

    public static AbstractDwellingType fromCode(String code) {
        for (Class<? extends AbstractDwellingType> type : ENUM_TYPES) {
            if (type.isEnum()) {
                var result = enumFromCode(type, code);
                if (result != null) return result;
            }
        }
        throw new IllegalArgumentException("Unknown DwellingType code: " + code);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & AbstractDwellingType> AbstractDwellingType enumFromCode(
            Class<? extends AbstractDwellingType> type, String code) {
        Class<E> enumClass = (Class<E>) type;
        try {
            return Enum.valueOf(enumClass, code);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static AbstractDwellingType findDwellingByCreatureType(
            CreatureType creatureType) {
        for (Class<? extends AbstractDwellingType> subtype : ENUM_TYPES) {
            if (!subtype.isEnum()) continue;
            var enumConstants = subtype.getEnumConstants();
            for (AbstractDwellingType constant : enumConstants) {
                    if (constant.getCreature().equals(creatureType)) {
                        return constant;
                    }
                }
            }
        throw new IllegalArgumentException(
                "No dwelling type found for creature " + creatureType);
    }
}
