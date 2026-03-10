package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import static ua.hudyma.domain.creatures.enums.CastleCreatureType.*;
import static ua.hudyma.domain.creatures.enums.CastleEssentialCreatureType.*;
import static ua.hudyma.domain.creatures.enums.RampartCreatureType.BATTLE_DWARF;
import static ua.hudyma.domain.creatures.enums.RampartEssentialCreatureType.DWARF;

@Getter
@RequiredArgsConstructor
public enum RampartDwellingType implements AbstractDwellingType {
    DWARF_COTTAGE (DWARF, BATTLE_DWARF);
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
