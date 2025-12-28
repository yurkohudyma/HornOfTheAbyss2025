package ua.hudyma.domain.heroes.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ua.hudyma.domain.heroes.enums.EntityField.*;

@Getter
@RequiredArgsConstructor
public enum ArtifactSlot {
    AMMO (PROPRIETORY),
    CAPE(PROPRIETORY),
    CATAPULT(PROPRIETORY),
    FEET(PROPRIETORY),
    FIRST_AID(PROPRIETORY),
    HAND_SHIELD(BODY),
    HAND_WEAPON(BODY),
    HELMET(PROPRIETORY),
    INVENTORY(BACKPACK),
    MISC_A(MISC),
    MISC_B(MISC),
    MISC_C(MISC),
    MISC_D(MISC),
    MISC_E(MISC),
    MISC_ANY(MISC),
    NECKLACE(BODY),
    RING_LEFT(BODY),
    RING_RIGHT(BODY),
    RING_ANY(BODY),
    SPELLBOOK(PROPRIETORY),
    TORSO(PROPRIETORY),
    WARMACHINE(PROPRIETORY); // BALLISTA CANNON
    private final EntityField entityField;
}
