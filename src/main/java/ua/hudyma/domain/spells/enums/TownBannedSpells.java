
package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import java.util.Set;

import static ua.hudyma.domain.spells.enums.EarthSpellSchool.ANIMATE_DEAD;
import static ua.hudyma.domain.spells.enums.EarthSpellSchool.DEATH_RIPPLE;
import static ua.hudyma.domain.spells.enums.FireSpellSchool.*;

@RequiredArgsConstructor
@Getter
public enum TownBannedSpells {
    CASTLE (Set.of(CURSE, BLOODLUST, DEATH_RIPPLE, ANIMATE_DEAD, ARMAGEDDON)),
    RAMPART (Set.of()),
    TOWER (Set.of()),
    INFERNO (Set.of()),
    NECROPOLIS (Set.of()),
    DUNGEON (Set.of()),
    STRONGHOLD (Set.of()),
    FORTRESS (Set.of()),
    CONFLUX (Set.of()),
    COVE (Set.of()),
    FACTORY (Set.of()),
    BULWARK (Set.of());

    private final Set<AbstractSpellSchool> bannedSpellsSet;
}
