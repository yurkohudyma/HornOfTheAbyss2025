package ua.hudyma.resource.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ua.hudyma.resource.enums.ResourceType.*;

@Getter
@RequiredArgsConstructor
public enum MineType {
    GOLDEN_MINE (GOLD, 1000),
    ALCHEMIST_LAB (MERCURY, 1),
    GEM_MINE (GEMS, 1),
    SAWMILL (WOOD, 2),
    QUARRY (ORE, 2),
    CRYSTAL_MINE (CRYSTAL, 1),
    SULFUR_MINE (SULFUR, 1);
    private final ResourceType resourceType;
    private final Integer weeklyProductionRate;
}
