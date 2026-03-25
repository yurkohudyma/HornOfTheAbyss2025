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
public enum RampartCreatureType implements CreatureType {
    CENTAUR_CAPTAIN (1, toResourceEnumMap(
            Map.of(
                    GOLD, 90))),
    BATTLE_DWARF (2, toResourceEnumMap(
            Map.of(
                    GOLD, 150))),
    GRAND_ELF (3, toResourceEnumMap(
            Map.of(
                    GOLD, 225))),
    SILVER_PEGASUS (4, toResourceEnumMap(
            Map.of(
                    GOLD, 275))),
    DENDROID_SOLDIER (5, toResourceEnumMap(
            Map.of(
                    GOLD, 425))),
    WAR_UNICORN (6, toResourceEnumMap(
            Map.of(
                    GOLD, 950))),
    GOLD_DRAGON (7, toResourceEnumMap(
            Map.of(
                    GOLD, 4000,
                    CRYSTAL, 2)));
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
