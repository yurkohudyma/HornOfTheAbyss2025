package ua.hudyma.domain.heroes.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.enums.Faction;

import java.util.EnumSet;

import static ua.hudyma.domain.heroes.enums.HeroSubfaction.*;
import static ua.hudyma.enums.Faction.*;
import static java.util.EnumSet.of;

@RequiredArgsConstructor
@Getter
public enum HeroFaction {
    CASTLE_HERO (CASTLE, of(KNIGHT, CLERIC)),
    RAMPART_HERO(RAMPART, of(RANGER, DRUID)),
    TOWER_HERO (TOWER, of(ALCHEMIST, WIZARD)),
    INFERNO_HERO (INFERNO, of(DEMONIAC, HERETIC)),
    NECROPOLIS_HERO (NECROPOLIS, of(DEATH_KNIGHT, NECROMANCER)),
    DUNGEON_HERO (DUNGEON, of(OVERLORD, WARLOCK)),
    STRONGHOLD_HERO (STRONGHOLD, of(BARBARIAN, BATTLE_MAGE)),
    FORTRESS_HERO (FORTRESS, of(BEASTMASTER, WITCH)),
    CONFLUX_HERO (CONFLUX, of(PLANESWALKER, ELEMENTALIST));
    private final Faction faction;
    private final EnumSet<HeroSubfaction> subfactions;
}

