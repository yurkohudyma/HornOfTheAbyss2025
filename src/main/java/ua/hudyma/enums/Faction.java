package ua.hudyma.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Faction {
    CASTLE (4),
    BULWARK (4),
    RAMPART (5),
    TOWER (5),
    INFERNO (5),
    NECROPOLIS (5),
    DUNGEON (5),
    STRONGHOLD (3),
    FORTRESS (3),
    CONFLUX (5),
    COVE (4),
    FACTORY (5);

    private final Integer magicGuildMaxLevel;
}
