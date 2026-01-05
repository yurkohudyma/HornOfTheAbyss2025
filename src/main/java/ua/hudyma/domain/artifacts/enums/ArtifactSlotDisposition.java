package ua.hudyma.domain.artifacts.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.enums.ArtifactSlot;

import static ua.hudyma.domain.heroes.enums.ArtifactSlot.*;

@Getter
@RequiredArgsConstructor
public enum ArtifactSlotDisposition {
    TOME_OF_EARTH_MAGIC (MISC_ANY,null),
    TOME_OF_AIR_MAGIC (MISC_ANY,null),
    TOME_OF_WATER_MAGIC (MISC_ANY,null),
    TOME_OF_FIRE_MAGIC (MISC_ANY,null),
    THUNDER_HELMET(HELMET, null),
    PENDANT_OF_COURAGE(NECKLACE, null),
    SANDALS_OF_THE_SAINT(FEET, null),
    ADMIRAL_HAT(HELMET, null),
    SENTINEL_SHIELD(HAND_SHIELD, null),
    LION_SHIELD_OF_COURAGE(HAND_SHIELD, null),
    TITAN_CUIRASS(TORSO, null),
    ANGEL_WINGS(CAPE, null),
    EQUESTRIAN_GLOVES(RING_ANY, RING_LEFT),
    STILL_EYE_OF_THE_DRAGON(RING_ANY, RING_RIGHT),
    TITAN_GLADIUS(HAND_WEAPON, null),

    //misc
    ORB_OF_VULNERABILITY(MISC_ANY, MISC_A),
    SHAMAN_PUPPET(MISC_ANY, MISC_B),
    SPECULUM(MISC_ANY, MISC_C),
    GOLDEN_BOW(MISC_ANY, MISC_D),
    SHACKLES_OF_WAR(MISC_ANY, MISC_E),

    //backpack_inventory:
    SLEEPKEEPER(INVENTORY, null),
    HORN_OF_THE_ABYSS(INVENTORY, null);
    private final ArtifactSlot artifactSlot;
    private final ArtifactSlot specificArtifactSlot;
}
