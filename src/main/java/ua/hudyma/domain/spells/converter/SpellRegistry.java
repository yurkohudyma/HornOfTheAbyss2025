package ua.hudyma.domain.spells.converter;

import org.reflections.Reflections;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.spells.AbstractSpellSchool;
import ua.hudyma.domain.spells.enums.properties.AbstractSpellProperty;

import java.util.Arrays;
import java.util.Set;

public class SpellRegistry {
    private SpellRegistry() {}
    private static final Reflections reflections;
    private static final Set<Class<? extends AbstractSpellSchool>> SCHOOL_ENUMS;
    private static final Set<Class<? extends AbstractSpellProperty>> PROP_ENUMS;

    static {
        reflections = new Reflections("ua.hudyma.domain.spells.enums");
        SCHOOL_ENUMS = reflections.getSubTypesOf(AbstractSpellSchool.class);
        PROP_ENUMS = reflections.getSubTypesOf(AbstractSpellProperty.class);
    }

    public static AbstractSpellSchool fromCode(String code) {
        for (Class<? extends AbstractSpellSchool> type : SCHOOL_ENUMS) {
            if (type.isEnum()) {
                var result = enumFromCode(type, code);
                if (result != null) return result;
            }
        }
        throw new IllegalArgumentException(
                "Unknown AbstractSpellSchool code: " + code);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & AbstractSpellSchool>
    AbstractSpellSchool enumFromCode(Class<? extends AbstractSpellSchool> type,
                                     String code) {
        Class<E> enumClass = (Class<E>) type;
        try {
            return Enum.valueOf(enumClass, code);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    public static AbstractSpellProperty fromCodeProperty(String code) {
        for (Class<? extends AbstractSpellProperty> type : PROP_ENUMS) {
            if (type.isEnum()) {
                var result = enumFromCodeProperty(type, code);
                if (result != null) return result;
            }
        }
        throw new IllegalArgumentException("Unknown AbstractSpellProperty code: " + code);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E> & AbstractSpellProperty> AbstractSpellProperty
    enumFromCodeProperty(Class<? extends AbstractSpellProperty> type, String code) {
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
            AbstractSpellSchool spellSchool, Class<B> baseInterface) {
        for (Class<? extends AbstractSpellSchool> subtype : SCHOOL_ENUMS) {
            if (subtype.isEnum() && Arrays.asList(subtype.getEnumConstants())
                    .contains(spellSchool)) {
                return (Class<? extends Enum<?>>) subtype;
            }
        }
        throw new IllegalArgumentException("No enum class matches name: " + spellSchool + " for "
                + baseInterface.getSimpleName());
    }
}
