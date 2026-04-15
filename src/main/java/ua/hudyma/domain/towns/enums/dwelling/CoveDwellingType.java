package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.EnumSet;
import java.util.Set;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CoveEssentialCreatureType.*;
import static ua.hudyma.domain.creatures.enums.creaturetypes.CoveCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum CoveDwellingType implements AbstractDwellingType {
    WATERFALL (NYMPH),
    UPG_WATERFALL (OCEANID),
    SHACK (CREW_MATE),
    UPG_SHACK (SEAMAN),
    FRIGATE (PIRATE),
    UPG_FRIGATE (CORSAIR),
    WAREHOUSE (SEA_DOG),
    NEST (STORMBIRD),
    UPG_NEST (AYSSID),
    TOWER_OF_THE_SEAS (SEA_WITCH),
    UPG_TOWER_OF_THE_SEAS(SORCERESS),
    NIX_PORT (NIX),
    UPG_NIX_PORT (NIX_WARRIOR),
    MAELSTROM (SEA_SERPENT),
    UPG_MAELSTROM (HASPID);

    private final CreatureType creature;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public CreatureType getCreature() {
        return creature;
    }
}
