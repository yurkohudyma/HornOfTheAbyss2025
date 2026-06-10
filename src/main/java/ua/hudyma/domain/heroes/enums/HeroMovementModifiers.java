package ua.hudyma.domain.heroes.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ua.hudyma.domain.heroes.enums.EnvironmentType.LAND;
import static ua.hudyma.domain.heroes.enums.EnvironmentType.WATER;

@Getter
@RequiredArgsConstructor
public enum HeroMovementModifiers {
    BOOTS_OF_SPEED (400, LAND),
    EQUESTRIAN_GLOVES (200, LAND),
    NECKLACE_OF_OCEAN_GUIDANCE (1000, WATER),
    SEA_CAPTAINS_HAT (500, WATER);

    private final Integer movePoints;
    private final EnvironmentType environmentType;

}
