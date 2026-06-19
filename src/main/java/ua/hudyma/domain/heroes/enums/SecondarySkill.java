package ua.hudyma.domain.heroes.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecondarySkill {
    AIR_MAGIC (new int[]{}),
    ARCHERY (new int[]{10, 25, 50}),
    ARMORER (new int[]{5, 10, 15}),
    ARTILLERY (new int[]{50, 75, 100}), //https://heroes.thelazy.net/index.php/Artillery

    /*Basic: The Ballista has a 50% chance to inflict 200% base Damage.
    The Cannon has an increased chance to hit fortifications.
    The Ballista and the Cannon cannot be dealt more than 40% of their Health as Damage from any single damage source.
    Advanced: The Ballista shoots twice and has a 75% chance to inflict 200% base Damage. The Cannon has an increased chance to hit fortifications and inflicts 200% base Damage to creatures. The Ballista and the Cannon cannot be dealt more than 40% of their Health as Damage from any single damage source. Horn of the Abyss
    Expert: The Ballista shoots twice and has a 100% chance to inflict 200% base Damage. The Cannon precisely hits fortifications at maximum Damage and inflicts 300% base Damage to creatures. The Ballista and the Cannon cannot be dealt more than 40% of their Health as Damage from any single damage source.*/


    BALLISTICS (new int[]{}), //https://heroes.thelazy.net/index.php/Ballistics
    DIPLOMACY(new int[]{20, 40, 60}),
    EAGLE_EYE (new int[]{30, 40, 50}),
    EARTH_MAGIC (new int[]{}),
    ESTATES (new int[]{250, 500, 1000}),
    FIRE_MAGIC (new int[]{}),
    FIRST_AID (new int[]{5, 10, 15}),
    INTELLIGENCE (new int[]{20, 35, 50}),
    LEADERSHIP (new int[]{1, 2, 3}),
    LEARNING (new int[]{20, 40, 60}),
    LOGISTICS (new int[]{5,10,20}),
    LUCK (new int[]{1, 2, 3}),
    MYSTICISM (new int[]{5, 10, 15}),
    NAVIGATION (new int[]{50, 100, 150}),
    NECROMANCY (new int[]{5, 10, 15, 20}), //20% for soul_prison_effect
    OFFENSE (new int[]{10, 20, 30}),
    PATHFINDING (new int[]{25, 50, 75}),
    RESISTANCE (new int[]{5, 10, 20}),
    SCHOLAR (new int[]{2, 3, 4}),
    SCOUTING (new int[]{1, 3, 5}),
    SORCERY (new int[]{10, 20, 30}),
    TACTICS (new int[]{3, 5, 7}),
    WATER_MAGIC (new int[]{}),
    WISDOM (new int[]{3, 4, 5});

    //https://heroes.thelazy.net/index.php/Secondary_skill

    private final int [] skillLevelModifiers;

    }
