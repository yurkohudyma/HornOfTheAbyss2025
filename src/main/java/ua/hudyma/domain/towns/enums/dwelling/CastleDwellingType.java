package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.Set;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType.*;
import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleEssentialCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum CastleDwellingType implements AbstractDwellingType {
    GUARDHOUSE (PIKEMEN,
            HALBERDIER,
            Set.of()),
    ARCHER_TOWER (ARCHER,
            MARKSMAN,
            Set.of()),
    GRIFFIN_TOWER (GRIFFIN,
            ROYAL_GRIFFIN,
            Set.of()),
    BARRACKS (SWORDSMAN,
            CRUSADER,
            Set.of()),
    MONASTERY (MONK,
            ZEALOT,
            Set.of()),
    TRAINING_GROUNDS (CAVALIER,
            CHAMPION,
            Set.of()),
    PORTAL_OF_GLORY (ANGEL,
            ARCHANGEL,
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
