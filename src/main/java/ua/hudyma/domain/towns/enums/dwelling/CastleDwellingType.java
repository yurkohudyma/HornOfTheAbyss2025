package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType.*;
import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleEssentialCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum CastleDwellingType implements AbstractDwellingType {
    GUARDHOUSE (PIKEMEN),
    UPG_GUARDHOUSE (HALBERDIER),
    ARCHER_TOWER (ARCHER),
    UPG_ARCHER_TOWER (MARKSMAN),
    GRIFFIN_TOWER (GRIFFIN),
    UPG_GRIFFIN_TOWER (ROYAL_GRIFFIN),
    BARRACKS (SWORDSMAN),
    UPG_BARRACKS (CRUSADER),
    MONASTERY (MONK),
    UPG_MONASTERY (ZEALOT),
    TRAINING_GROUNDS (CAVALIER),
    UPG_TRAINING_GROUNDS (CHAMPION),
    PORTAL_OF_GLORY (ANGEL),
    UPG_PORTAL_OF_GLORY (ARCHANGEL);
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
