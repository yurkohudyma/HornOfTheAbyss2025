package ua.hudyma.domain.creatures.enums.creaturetypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.domain.resource.enums.ResourceType.GOLD;
import static ua.hudyma.domain.resource.enums.ResourceType.MERCURY;

@Getter
@RequiredArgsConstructor
public enum InfernoCreatureType implements CreatureType {
    ARCHDEVIL (7, toResourceEnumMap(Map.of(
            GOLD, 4500,
            MERCURY, 2
    ))),
    EFREET_SULTAN(6, toResourceEnumMap(Map.of(
            GOLD, 1100
    ))),
    PIT_LORD(6, toResourceEnumMap(Map.of(
            GOLD, 700
    ))),
    HORNED_DEMON(4, toResourceEnumMap(Map.of(
            GOLD, 270
    ))),
    CERBEROS(3, toResourceEnumMap(Map.of(
            GOLD, 250
    ))),
    MAGOG(2, toResourceEnumMap(Map.of(
            GOLD, 175
    ))),
    FAMILIAR(1, toResourceEnumMap(Map.of(
            GOLD, 60
    )));
    private final Integer level;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;

    //todo populate resources demand

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public EnumMap<ResourceType, Integer> getRequiredResourceMap() {
        return null;
    }

    @Override
    public Integer creatureBoost() {
        return 0;
    }

    private static <T extends Enum<T>> EnumMap<T, Integer> emptyMap() {
        return new EnumMap<>((Class<T>) ResourceType.class);
    }

    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll(resources);
        return map;
    }
}
