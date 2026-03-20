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
public enum CoveEssentialCreatureType implements CreatureType {
    SEA_SERPENT(7, toResourceEnumMap(
            Map.of(
                    SULFUR, 1,
                    GOLD, 2200))),
    NIX(6, toResourceEnumMap(
            Map.of(
                    GOLD, 1000))),
    SEA_WITCH(5, toResourceEnumMap(
            Map.of(
                    GOLD, 515))),
    STORMBIRD(4,toResourceEnumMap(
            Map.of(
                    GOLD, 275))),
    PIRATE(3, toResourceEnumMap(
            Map.of(
                    GOLD, 225))),
    CREW_MATE(2, toResourceEnumMap(
            Map.of(
                    GOLD, 110))),
    NYMPH (1, toResourceEnumMap(
            Map.of(
                    GOLD, 35)));
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
