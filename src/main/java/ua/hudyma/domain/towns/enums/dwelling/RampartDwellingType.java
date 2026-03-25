package ua.hudyma.domain.towns.enums.dwelling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.Set;

import static ua.hudyma.domain.creatures.enums.creaturetypes.RampartCreatureType.*;
import static ua.hudyma.domain.creatures.enums.creaturetypes.RampartEssentialCreatureType.*;

@Getter
@RequiredArgsConstructor
public enum RampartDwellingType implements AbstractDwellingType {
    CENTAUR_STABLES (CENTAUR,
            CENTAUR_CAPTAIN,
            Set.of()),
    DWARF_COTTAGE (DWARF,
            BATTLE_DWARF,
            Set.of()),
    HOMESTEAD (WOOD_ELF,
            GRAND_ELF,
            Set.of()),
    ENCHANTED_SPRING (PEGASUS,
            SILVER_PEGASUS,
            Set.of()),
    DENDROID_ARCHES (DENDROID_GUARD,
            DENDROID_SOLDIER,
            Set.of()),
    UNICORN_GLADE (UNICORN,
            WAR_UNICORN, Set.of()),
    DRAGON_CLIFFS (GREEN_DRAGON,
            GOLD_DRAGON,
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
