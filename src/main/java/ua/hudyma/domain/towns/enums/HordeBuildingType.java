package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.enums.Faction;

import static ua.hudyma.domain.creatures.enums.CastleCreatureType.ROYAL_GRIFFIN;
import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum HordeBuildingType implements AbstractBuildingType{
    BIRTHING_POOL (INFERNO, null, 0),
    CAPTAINS_QUARTERS(FORTRESS, null, 0),
    CAGES(INFERNO, null, 0),
    DENDROID_SAPLINGS (RAMPART, null, 0),
    GARDEN_OF_LIFE (CONFLUX, null, 0),
    GRIFFIN_BASTION (CASTLE, ROYAL_GRIFFIN, 3),
    MESS_HALL(STRONGHOLD, null, 0),
    MINERS_GUILD (RAMPART, null, 0),
    MUSHROOM_RINGS (DUNGEON, null, 0),
    PEN(STRONGHOLD, null, 0),
    PUB(COVE, null, 0),
    ROOST(COVE, null, 0),
    SCULPTORS_WINGS(TOWER, null, 0),
    UNEARTHED_GRAVES(NECROPOLIS, null, 0),
    VAULT_OF_ASHES(CONFLUX, null, 0);
    private final Faction faction;
    private final CreatureType creatureType;
    private final Integer creatureBoost;
}
