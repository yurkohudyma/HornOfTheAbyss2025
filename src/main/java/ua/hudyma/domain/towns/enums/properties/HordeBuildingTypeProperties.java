package ua.hudyma.domain.towns.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static ua.hudyma.domain.towns.enums.dwelling.CastleDwellingType.GRIFFIN_TOWER;
import static ua.hudyma.resource.enums.ResourceType.GOLD;

@Getter
@RequiredArgsConstructor
public enum HordeBuildingTypeProperties implements AbstractBuildingTypeProperties {
    GRIFFIN_BASTION (Set.of(GRIFFIN_TOWER.name()),
            toResourceEnumMap(
                    Map.of(
                            GOLD, 2500)));
    /*MESS_HALL(STRONGHOLD),
    MINERS_GUILD (RAMPART),
    MUSHROOM_RINGS (DUNGEON),
    PEN(STRONGHOLD),
    PUB(COVE),
    ROOST(COVE),
    SCULPTORS_WINGS(TOWER),
    UNEARTHED_GRAVES(NECROPOLIS),
    VAULT_OF_ASHES(CONFLUX);*/;
    /*BIRTHING_POOL (INFERNO),
    CAPTAINS_QUARTERS(FORTRESS),
    CAGES(INFERNO),
    DENDROID_SAPLINGS (RAMPART),
    GARDEN_OF_LIFE (CONFLUX),*/
    private final Set<String> requiredBuildingSet;
    private final EnumMap<ResourceType, Integer> requiredResourceMap;
    private static EnumMap<ResourceType, Integer> toResourceEnumMap(
            Map<ResourceType, Integer> resources) {
        var map = new EnumMap<ResourceType, Integer>(ResourceType.class);
        map.putAll((resources));
        return map;
    }
}
