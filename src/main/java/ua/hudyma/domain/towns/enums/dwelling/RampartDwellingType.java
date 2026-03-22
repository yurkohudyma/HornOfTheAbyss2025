package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.Set;

import static ua.hudyma.domain.creatures.enums.creaturetypes.RampartCreatureType.BATTLE_DWARF;
import static ua.hudyma.domain.creatures.enums.creaturetypes.RampartEssentialCreatureType.DWARF;

@Getter
@RequiredArgsConstructor
public enum RampartDwellingType implements AbstractDwellingType {
    DWARF_COTTAGE (DWARF,
            BATTLE_DWARF,
            Set.of());
    //todo populate rampart
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
