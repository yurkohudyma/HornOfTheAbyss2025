package ua.hudyma.domain.creatures.enums.creaturetypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.resource.enums.ResourceType;

import java.util.EnumMap;

@Getter
@RequiredArgsConstructor
public enum InfernoEssentialCreatureType implements CreatureType {
    DEVIL(7, emptyMap()),
    EFREETE(6, emptyMap()),
    PIT_FIEND(5, emptyMap()),
    DEMON(4, emptyMap()),
    HELL_HOUND(3, emptyMap()),
    GOG(2, emptyMap()),
    IMP(1, emptyMap());

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
}
