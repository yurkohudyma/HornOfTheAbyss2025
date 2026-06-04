package ua.hudyma.domain.creatures.enums.creaturetypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;

@Getter
@RequiredArgsConstructor
public enum InfernoCreatureType implements CreatureType {
    ARCHDEVIL (7, emptyMap()),
    EFREET_SULTAN(6, emptyMap()),
    PIT_LORD(6, emptyMap()),
    HORNED_DEMON(4, emptyMap()),
    CERBEROS(3, emptyMap()),
    MAGOG(2, emptyMap()),
    FAMILIAR(1, emptyMap());
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
}
