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
public enum CastleEssentialCreatureType implements CreatureType {

    //todo correct numbers lazy.net
    ANGEL(7, toResourceEnumMap(
            Map.of(
                    GEMS, 3,
                    GOLD, 5000))),
    CAVALIER(6, toResourceEnumMap(
            Map.of(
                    GOLD, 1200))),
    MONK(5, toResourceEnumMap(
            Map.of(
                    GOLD, 450))),
    SWORDSMAN(4,toResourceEnumMap(
            Map.of(
                    GOLD, 400))),
    GRIFFIN(3, toResourceEnumMap(
            Map.of(
                    GOLD, 240))),
    ARCHER(2, toResourceEnumMap(
            Map.of(
                    GOLD, 150))),
    PIKEMEN (1, toResourceEnumMap(
            Map.of(
                    GOLD, 75)));
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
    public String getCode() {
        return name();
    }
}
