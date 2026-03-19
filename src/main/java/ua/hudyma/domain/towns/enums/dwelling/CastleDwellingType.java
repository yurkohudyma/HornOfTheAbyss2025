package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType.*;
import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleEssentialCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum CastleDwellingType implements AbstractDwellingType {
    GUARDHOUSE (PIKEMEN, HALBERDIER),
    ARCHER_TOWER (ARCHER, MARKSMAN),
    GRIFFIN_TOWER (GRIFFIN, ROYAL_GRIFFIN),
    BARRACKS (SWORDSMAN, CRUSADER),
    MONASTERY (MONK, ZEALOT),
    TRAINING_GROUNDS (CAVALIER, CHAMPION),
    PORTAL_OF_GLORY (ANGEL, ARCHANGEL);
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
