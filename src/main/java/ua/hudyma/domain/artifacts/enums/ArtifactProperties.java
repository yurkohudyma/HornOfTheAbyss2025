package ua.hudyma.domain.artifacts.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static ua.hudyma.domain.artifacts.enums.ArtifactAction.*;
import static ua.hudyma.domain.creatures.enums.CreatureProperty.*;
import static ua.hudyma.domain.creatures.enums.MovementType.FLYING;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.*;

@Getter
@RequiredArgsConstructor
public enum ArtifactProperties {
    PENDANT_OF_COURAGE(BOOST, Map.of(
            LUCK.name(), 3,
            MORALE.name(), 3)),
    SANDALS_OF_THE_SAINT(BOOST, Map.of(
            ATTACK.name(), 2,
            DEFENSE.name(), 2,
            POWER.name(), 2,
            KNOWLEDGE.name(), 2)),
    ADMIRAL_HAT(COMPLEX, Map.of()), //No boarding or unboarding penalty for boats. Converts movement points between land and water movement. +1500 movement points on water. Gain Summon Boat and Scuttle Boat while equipped.Protects army in whirlpools.
    SENTINEL_SHIELD(BOOST,
            Map.of(
                    ATTACK.name(), -3,
                    DEFENSE.name(), 12)),
    TITAN_CUIRASS(BOOST, Map.of(
            POWER.name(), 10,
            KNOWLEDGE.name(), -2)),
    ANGEL_WINGS(MODIFIER,
            Map.of(MOVEMENT.name(), FLYING)),
    EQUESTRIAN_GLOVES(BOOST, Map.of(
            LAND_MOVEMENT.name(), 200)),
    STILL_EYE_OF_THE_DRAGON(BOOST, Map.of(
            LUCK.name(), 1,
            MORALE.name(), 1)),
    TITAN_GLADIUS(BOOST,
            Map.of(
                    ATTACK.name(), 12,
                    DEFENSE.name(), -3)),
    //misc
    ORB_OF_VULNERABILITY(COMPLEX, Map.of()), //Negate natural magic resistance, magic immunity and chances to resist spells of all creatures on the battlefield. Does not affect the limitations on applicability stated in spell descriptions as well as the immunities given by artifacts.
    SHAMAN_PUPPET(ENEMY_BOOST, Map.of(LUCK.name(), -2)),
    SPECULUM(BOOST, Map.of(VISIBILITY.name(), 1)),
    GOLDEN_BOW(COMPLEX, Map.of()), //No range penalty. No obstacle penalty.
    SHACKLES_OF_WAR(COMPLEX, Map.of()), //Neither hero may retreat or surrender in combat.

    //backpack_inventory:
    SLEEPKEEPER(COMPLEX, Map.of()), //Immunity to mind spells.
    HORN_OF_THE_ABYSS(COMPLEX, Map.of()); //After a stack of living creatures is slain, a stack of Fangarms will rise in their stead and will stay loyal to the hero after the battle concludes
    private final ArtifactAction artifactAction;
    private final Map<String, Object> actionData;
}
