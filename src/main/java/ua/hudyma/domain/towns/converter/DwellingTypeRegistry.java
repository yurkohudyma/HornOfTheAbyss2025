package ua.hudyma.domain.towns.converter;

import org.reflections.Reflections;
import ua.hudyma.domain.towns.enums.DwellingType;

import java.util.Arrays;
import java.util.Set;

public class DwellingTypeRegistry {
    private static final Reflections reflections;
    private static final Set<Class<? extends DwellingType>> ENUM_TYPES;

    private DwellingTypeRegistry (){}

    static {
        reflections = new Reflections("ua.hudyma.domain.towns.enums");
        ENUM_TYPES = reflections.getSubTypesOf(DwellingType.class);
    }

    public static DwellingType fromCode(String code) {
        for (Class<? extends DwellingType> type : ENUM_TYPES) {
            if (type.isEnum()) {
                var result = enumFromCode(type, code);
                if (result != null) return result;
            }
        }
        throw new IllegalArgumentException("Unknown DwellingType code: " + code);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E> & DwellingType> DwellingType enumFromCode(
            Class<? extends DwellingType> type, String code) {
        Class<E> enumClass = (Class<E>) type;
        try {
            return Enum.valueOf(enumClass, code);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Пошук enum-класу серед усіх підтипів базового інтерфейсу/класу.
     *
     * @param type          частина назви enum-класу для пошуку
     * @param baseInterface інтерфейс або базовий клас, який реалізує enum
     * @param <B>           тип базового інтерфейсу
     * @return enum-клас, який реалізує B
     */
    @SuppressWarnings("unchecked")
    public static <B> Class<? extends Enum<?>> findEnumClassByChildName(
            DwellingType type, Class<B> baseInterface) {
        Set<Class<? extends B>> subtypes = reflections
                .getSubTypesOf(baseInterface);
        for (Class<? extends B> subtype : subtypes) {
            if (subtype.isEnum() && Arrays.asList(subtype
                    .getEnumConstants()).contains(type)) {
                return (Class<? extends Enum<?>>) subtype;
            }
        }
        throw new IllegalArgumentException("No enum class matches name: " + type + " for "
                + baseInterface.getSimpleName());
    }
}
