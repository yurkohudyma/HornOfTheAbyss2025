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

    /**
     * Пошук enum-класу за типом істоти серед усіх підтипів базового інтерфейсу/класу.
     *
     * @param baseInterface інтерфейс або базовий клас, який реалізує enum
     * @param <B>           тип базового інтерфейсу
     * @return enum-клас, який реалізує B
     */
   /* @SuppressWarnings("unchecked")
    public static <B extends AbstractDwellingType> Class<? extends Enum<?>> findEnumClassByCreatureType(
            Class<B> baseInterface, CreatureType creatureType) {
        Set<Class<? extends B>> subtypes = reflections
                .getSubTypesOf(baseInterface);
        for (Class<? extends B> subtype : subtypes) {
            var enumConstants = subtype
                    .getEnumConstants();
            if (subtype.isEnum())
                return getDwellingTypeContainingCreature(enumConstants, creatureType);
        }
        throw new IllegalArgumentException("No enum class matches name for "
                + baseInterface.getSimpleName());
    }*/

    public static AbstractDwellingType findByCreatureType(
            CreatureType creatureType) {
        Set<Class<? extends AbstractDwellingType>> subtypes =
                reflections.getSubTypesOf(AbstractDwellingType.class);
        for (Class<? extends AbstractDwellingType> subtype : subtypes) {
            if (!subtype.isEnum()) continue;
            AbstractDwellingType[] enumConstants =
                    subtype.getEnumConstants();
            for (AbstractDwellingType constant : enumConstants) {
                if (constant.getCreature().equals(creatureType)) {
                    return constant;
                }
            }
        }
        throw new IllegalArgumentException(
                "No dwelling type found for creature " + creatureType);
    }

    /*private static <B> B getDwellingTypeContainingCreature(
            B[] enumConstants, CreatureType creatureType) {
        for (B enumm : enumConstants) {
            if (((AbstractDwellingType) enumm).getCreature().equals(creatureType))
                return enumm;
        }
        throw new IllegalStateException("Dwelling type for Creature " + creatureType + " NOT found");
    }*/
}
