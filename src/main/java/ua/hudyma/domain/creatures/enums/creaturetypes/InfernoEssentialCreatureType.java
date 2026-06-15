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
public enum InfernoEssentialCreatureType implements CreatureType {
    DEVIL(7, toResourceEnumMap(Map.of(
            GOLD, 2700,
            MERCURY, 1
    ))),
    EFREETE(6, toResourceEnumMap(Map.of(
            GOLD, 900
    ))),
    PIT_FIEND(5, toResourceEnumMap(Map.of(
            GOLD, 500
    ))),
    DEMON(4, toResourceEnumMap(Map.of(
            GOLD, 250
    ))),
    HELL_HOUND(3, toResourceEnumMap(Map.of(
            GOLD, 200
    ))),
    GOG(2, toResourceEnumMap(Map.of(
            GOLD, 125
    ))),
    IMP(1, toResourceEnumMap(Map.of(
            GOLD, 50
    )));

    //todo populate resources demand
    private final Integer level;

    private final EnumMap<ResourceType, Integer> requiredResourceMap;

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
