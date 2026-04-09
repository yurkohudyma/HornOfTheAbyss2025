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
    CENTAUR_STABLES (CENTAUR),
    UPG_CENTAUR_STABLES (CENTAUR_CAPTAIN),
    DWARF_COTTAGE (DWARF),
    UPG_DWARF_COTTAGE (BATTLE_DWARF),
    HOMESTEAD (WOOD_ELF),
    UPG_HOMESTEAD (GRAND_ELF),
    ENCHANTED_SPRING (PEGASUS),
    UPG_ENCHANTED_SPRING (SILVER_PEGASUS),
    DENDROID_ARCHES (DENDROID_GUARD),
    UPG_DENDROID_ARCHES (DENDROID_SOLDIER),
    UNICORN_GLADE (UNICORN),
    UPG_UNICORN_GLADE (WAR_UNICORN),
    DRAGON_CLIFFS (GREEN_DRAGON),
    UPG_DRAGON_CLIFFS (GOLD_DRAGON);

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
