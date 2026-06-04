package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import static ua.hudyma.domain.creatures.enums.creaturetypes.InfernoCreatureType.*;
import static ua.hudyma.domain.creatures.enums.creaturetypes.InfernoEssentialCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum InfernoDwellingType implements AbstractDwellingType {
    IMP_CRUCIBLE(IMP),
    UPG_IMP_CRUCIBLE(FAMILIAR),
    HALL_OF_SINS(GOG),
    UPG_HALL_OF_SINS(MAGOG),
    KENNELS(HELL_HOUND),
    UPG_KENNELS(CERBEROS),
    DEMON_GATE(DEMON),
    UPG_DEMON_GATE(HORNED_DEMON),
    HELL_HOLE(PIT_FIEND),
    UPG_HELL_HOLE(PIT_LORD),
    FIRE_LAKE(EFREETE),
    UPG_FIRE_LAKE(EFREET_SULTAN),
    FORSAKEN_PALACE(DEVIL),
    UPG_FORSAKEN_PALACE(ARCHDEVIL);

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
