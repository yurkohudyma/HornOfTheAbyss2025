package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.enums.Faction;

import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum GrailBuildingType implements AbstractBuildingType {
    AURORA_BOREALIS (CONFLUX),
    CARNIVOROUS_PLANT (FORTRESS),
    COLOSSUS (CASTLE),
    DEITY_OF_FIRE (CONFLUX),
    GUARDIAN_OF_EARTH (DUNGEON),
    LIGHTNING_ROD (FACTORY),
    LODESTAR (COVE),
    SKYSHIP (TOWER),
    SOUL_PRISON (INFERNO),
    SPIRIT_GUARDIAN (RAMPART),
    WARLORDS_MONUMENT(STRONGHOLD);
    private final Faction faction;
}
