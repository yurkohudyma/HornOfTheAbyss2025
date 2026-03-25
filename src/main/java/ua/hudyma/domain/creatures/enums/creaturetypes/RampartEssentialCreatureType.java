package ua.hudyma.domain.creatures.enums.creaturetypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.resource.enums.ResourceType.CRYSTAL;
import static ua.hudyma.resource.enums.ResourceType.GOLD;

@Getter
@RequiredArgsConstructor
public enum RampartEssentialCreatureType implements CreatureType {

    CENTAUR (1, toResourceEnumMap(
            Map.of(
                    GOLD, 70))),
    DWARF (2, toResourceEnumMap(
            Map.of(
                    GOLD, 120))),
    WOOD_ELF (3, toResourceEnumMap(
            Map.of(
                    GOLD, 200))),
    PEGASUS (4, toResourceEnumMap(
            Map.of(
                    GOLD, 250))),
    DENDROID_GUARD (5, toResourceEnumMap(
            Map.of(
                    GOLD, 350))),
    UNICORN (6, toResourceEnumMap(
            Map.of(
                    GOLD, 950))),
    GREEN_DRAGON (7, toResourceEnumMap(
            Map.of(
                    GOLD, 2400,
                    CRYSTAL, 1)));
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
