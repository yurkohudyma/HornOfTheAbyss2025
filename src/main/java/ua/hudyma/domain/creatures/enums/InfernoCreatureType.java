package ua.hudyma.domain.creatures.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;

@Getter
@RequiredArgsConstructor
public enum InfernoCreatureType implements CreatureType {
    ARCHDEVIL (7, emptyMap()),
    FAMILIAR(1, emptyMap());
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

    private static <T extends Enum<T>> EnumMap<T, Integer> emptyMap() {
        return new EnumMap<>((Class<T>) ResourceType.class);
    }
}
