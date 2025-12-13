package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.enums.Faction;

import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum UniqueBuildingType {

    //castle
    LIGHTHOUSE (CASTLE),
    STABLES (CASTLE),

    //inferno
    CASTLE_GATE (INFERNO),
    ORDER_OF_FIRE(INFERNO);
    private final Faction faction;
}
