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
    WATERFALL (NYMPH, OCEANID,
            Set.of()),
    SHACK (CREW_MATE, SEAMAN,
            Set.of()),
    WAREHOUSE (PIRATE, CORSAIR,
            Set.of(SEA_DOG)),
    NEST (STORMBIRD, AYSSID,
            Set.of()),
    TOWER_OF_THE_SEAS (SEA_WITCH, SORCERESS,
            Set.of()),
    NIX_PORT (NIX, NIX_WARRIOR,
            Set.of()),
    MAELSTROM (SEA_SERPENT, HASPID,
            Set.of());
    private final CreatureType essentialCreature;
    private final CreatureType creature;
    private final Set<CreatureType> creatureSet;

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

    @Override
    public Set<CreatureType> getCreatureSet(){
        return creatureSet;
    }
}
