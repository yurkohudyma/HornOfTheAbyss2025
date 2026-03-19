package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CoveEssentialCreatureType.*;
import static ua.hudyma.domain.creatures.enums.creaturetypes.CoveCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum CoveDwellingType implements AbstractDwellingType {
    WATERFALL (NYMPH, OCEANID),
    SHACK (CREW_MATE, SEAMAN),
    WAREHOUSE (PIRATE, CORSAIR),
    //todo refack into EnumSet<CreatureType> to include SEA_DOG
    NEST (STORMBIRD, AYSSID),
    TOWER_OF_THE_SEAS (SEA_WITCH, SORCERESS),
    NIX_PORT (NIX, NIX_WARRIOR),
    MAELSTROM (SEA_SERPENT, HASPID);
    private final CreatureType essentialCreature;
    private final CreatureType creature;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public CreatureType getCreature() {
        return creature;
    }

    @Override
    public CreatureType getEssentialCreature(){
        return essentialCreature;
    }
}
