package ua.hudyma.domain.towns.enums.dwelling;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.enums.CastleCreatureType;
import ua.hudyma.domain.towns.enums.AbstractBuildingType;

import static ua.hudyma.domain.creatures.enums.CastleCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum CastleDwellingType implements AbstractDwellingType {
    GUARDHOUSE (HALBERDIER),
    ARCHER_TOWER (MARKSMAN),
    GRIFFIN_TOWER (ROYAL_GRIFFIN),
    BARRACKS (CRUSADER),
    MONASTERY (ZEALOT),
    TRAINING_GROUNDS (CHAMPION),
    PORTAL_OF_GLORY (ARCHANGEL);
    private final CastleCreatureType creature;

    @Override
    public String getCode() {
        return name();
    }
}
