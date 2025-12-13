package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.enums.Faction;

import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum HordeBuildingType {
    BIRTHING_POOL (INFERNO),
    CAPTAINS_QUARTERS(FORTRESS),
    CAGES(INFERNO),
    DENDROID_SAPLINGS (RAMPART),
    GARDEN_OF_LIFE (CONFLUX),
    GRIFFIN_BASTION (CASTLE),
    MESS_HALL(STRONGHOLD),
    MINERS_GUILD (RAMPART),
    MUSHROOM_RINGS (DUNGEON),
    PEN(STRONGHOLD),
    PUB(COVE),
    ROOST(COVE),
    SCULPTORS_WINGS(TOWER),
    UNEARTHED_GRAVES(NECROPOLIS),
    VAULT_OF_ASHES(CONFLUX);

    private final Faction faction;
}
