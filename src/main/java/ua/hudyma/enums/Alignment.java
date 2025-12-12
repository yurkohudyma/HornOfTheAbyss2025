package ua.hudyma.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum Alignment {
    GOOD (EnumSet.of(CASTLE, RAMPART, TOWER)),
    EVIL (EnumSet.of(INFERNO, DUNGEON, NECROPOLIS)),
    NEUTRAL (EnumSet.of(STRONGHOLD, FORTRESS, COVE, FACTORY));
    private final EnumSet<Faction> faction;
}
