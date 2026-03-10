package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.enums.Faction;

import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum UniqueBuildingType implements AbstractBuildingType{

    //castle
    BROTHERHOOD_OF_THE_SWORD(CASTLE, 0),
    LIGHTHOUSE (CASTLE, 0),
    STABLES (CASTLE, 300),

    //rampart
    TREASURY (RAMPART, 10),

    //inferno
    CASTLE_GATE (INFERNO, 0),
    ORDER_OF_FIRE(INFERNO, 1);
    private final Faction faction;
    private final Integer value;
}
