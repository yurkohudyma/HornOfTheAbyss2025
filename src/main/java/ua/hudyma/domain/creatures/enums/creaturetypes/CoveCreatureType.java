package ua.hudyma.domain.creatures.enums.creaturetypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum CoveCreatureType implements CreatureType {
    HASPID(7, toResourceEnumMap(
            Map.of(
                    SULFUR, 2,
                    GOLD, 4000))),
    NIX_WARRIOR(6, toResourceEnumMap(
            Map.of(
                    GOLD, 1300))),
    SORCERESS(5, toResourceEnumMap(
            Map.of(
                    GOLD, 565))),
    AYSSID(4,toResourceEnumMap(
            Map.of(
                    GOLD, 325))),
    CORSAIR(3, toResourceEnumMap(
            Map.of(
                    GOLD, 275))),
    SEA_DOG(3, toResourceEnumMap(
            Map.of(
                    GOLD, 375))),
    SEAMAN(2, toResourceEnumMap(
            Map.of(
                    GOLD, 140))),
    OCEANID (1, toResourceEnumMap(
            Map.of(
                    GOLD, 45)));
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
