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
            Set.of(PIRATE, CORSAIR, SEA_DOG)),
    //todo review game mechanics to introduce checking
    // if (enum.getCreatureSet.isEmpty()) get essential or primary creature
    // further get rid of essentialCreature and creature fields, putting all
    // creatures into the set.

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
