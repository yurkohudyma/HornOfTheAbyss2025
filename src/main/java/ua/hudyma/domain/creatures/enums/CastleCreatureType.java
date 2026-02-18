package ua.hudyma.domain.creatures.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum CastleCreatureType implements CreatureType {
    ARCHANGEL(7, toResourceEnumMap(
            Map.of(
                    GEMS, 3,
                    GOLD, 5000))),
    CHAMPION(6, toResourceEnumMap(
            Map.of(
                    GOLD, 1200))),
    ZEALOT(5, toResourceEnumMap(
            Map.of(
                    GOLD, 450))),
    CRUSADER(4,toResourceEnumMap(
            Map.of(
                    GOLD, 400))),
    ROYAL_GRIFFIN(3, toResourceEnumMap(
            Map.of(
                    GOLD, 240))),
    MARKSMAN(2, toResourceEnumMap(
            Map.of(
                    GOLD, 150))),
    HALBERDIER (1, toResourceEnumMap(
            Map.of(
                    GOLD, 75)));
    private final Integer level;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;

    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll((resources));
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
