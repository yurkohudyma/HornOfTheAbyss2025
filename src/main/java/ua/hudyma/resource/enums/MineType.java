package ua.hudyma.resource.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum MineType {
    GOLDEN_MINE (GOLD, 1000),
    ALCHEMIST_LAB (MERCURY, 1),
    GEM_POND (GEMS, 1),
    SAWMILL (WOOD, 2),
    ORE_PIT (ORE, 2),
    CRYSTAL_CAVERN (CRYSTAL, 1),
    SULFUR_DUNE (SULFUR, 1);
    private final ResourceType resourceType;
    private final Integer weeklyProductionRate;
}
