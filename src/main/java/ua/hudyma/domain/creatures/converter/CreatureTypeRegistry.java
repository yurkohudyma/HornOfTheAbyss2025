package ua.hudyma.domain.creatures.converter;

import org.reflections.Reflections;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

public class CreatureTypeRegistry {
    private static final Reflections reflections;
    private static final Set<Class<? extends CreatureType>> ENUM_TYPES;

    static {
        reflections = new Reflections("ua.hudyma.domain.creatures.enums");
        ENUM_TYPES = reflections.getSubTypesOf(CreatureType.class);
    }

    public static CreatureType fromCode(String code) {
        for (Class<? extends CreatureType> type : ENUM_TYPES) {
            if (type.isEnum()) {
                var result = enumFromCode(type, code);
                if (result != null) return result;
            }
        }
        throw new IllegalArgumentException("Unknown CreatureType code: " + code);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E> & CreatureType> CreatureType enumFromCode(
            Class<? extends CreatureType> type, String code) {
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
            CreatureType type, Class<B> baseInterface) {
        Set<Class<? extends B>> subtypes = reflections.getSubTypesOf(baseInterface);
        for (Class<? extends B> subtype : subtypes) {
            if (subtype.isEnum() && Arrays.asList(subtype.getEnumConstants()).contains(type)) {
                return (Class<? extends Enum<?>>) subtype;
            }
        }
        throw new IllegalArgumentException("No enum class matches name: " + type + " for "
                + baseInterface.getSimpleName());
    }

    /**
     * Метод створює мапу: enum -> список рядкових значень його полів.
     *
     * @param enumClass enum-клас, що реалізує CreatureType
     * @param <T>       тип enum-класу
     * @return Map<T, Integer>
     */
    public static <T extends Enum<T> & CreatureType> Map<Integer, T> convertEnumToLevelMap(
            Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .sorted(comparing(T::getLevel))
                .collect(toMap(
                        T::getLevel,
                        e -> e,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
