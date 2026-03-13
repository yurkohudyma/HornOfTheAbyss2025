package ua.hudyma.domain.creatures.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.GEMS;
import static ua.hudyma.resource.enums.ResourceType.GOLD;

@Getter
@RequiredArgsConstructor
public enum RampartCreatureType implements CreatureType {

    BATTLE_DWARF (2, toResourceEnumMap(
            Map.of(
                    GOLD, 150)));
    private final Integer level;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;

    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll(resources);
        return map;
    }

    @Override
    public EnumMap<ResourceType, Integer> getRequiredResourceMap() {
        return requiredResourceMap;
    }

    @Override
    public Integer creatureBoost() {
        return 0;
    }

    @Override
    public String getCode() {
        return name();
    }
}
