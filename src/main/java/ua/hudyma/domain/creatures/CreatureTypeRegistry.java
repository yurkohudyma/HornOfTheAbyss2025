package ua.hudyma.domain.creatures;

import ua.hudyma.domain.creatures.enums.CastleCreatureType;
import ua.hudyma.domain.creatures.enums.InfernoCreatureType;

import java.util.List;

public class CreatureTypeRegistry {
    private static final List<Class<? extends CreatureType>> ENUM_TYPES = List.of(
            CastleCreatureType.class,
            InfernoCreatureType.class
    );

    public static CreatureType fromCode(String code) {
        for (Class<? extends CreatureType> type : ENUM_TYPES) {
            if (type.isEnum()) {
                CreatureType result = enumFromCode(type, code);
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
}

