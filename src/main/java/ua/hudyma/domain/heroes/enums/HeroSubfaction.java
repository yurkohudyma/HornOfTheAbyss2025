package ua.hudyma.domain.heroes.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HeroSubfaction {
    KNIGHT(new int[]{2, 2, 1, 1}),
    CLERIC(new int[]{1, 0, 2, 2}),
    RANGER(new int[]{1, 3, 1, 1}),
    DRUID(new int[]{0, 2, 1, 2}),
    ALCHEMIST(new int[]{1, 1, 2, 2}),
    WIZARD(new int[]{0, 0, 2, 3}),
    DEMONIAC(new int[]{2, 2, 1, 1}),
    HERETIC(new int[]{1, 1, 2, 1}),
    DEATH_KNIGHT(new int[]{1, 2, 2, 1}),
    NECROMANCER(new int[]{1, 0, 2, 2}),
    OVERLORD(new int[]{2, 2, 1, 1}),
    WARLOCK(new int[]{0, 0, 3, 2}),
    BARBARIAN(new int[]{4, 0, 1, 1}),
    BATTLE_MAGE(new int[]{2, 1, 1, 1}),
    BEASTMASTER(new int[]{0, 4, 1, 1}),
    WITCH(new int[]{0, 1, 2, 2}),
    PLANESWALKER(new int[]{3, 1, 1, 1}),
    ELEMENTALIST(new int[]{0, 0, 3, 3}),
    CAPTAIN(new int[]{3, 0, 2, 1}),
    NAVIGATOR(new int[]{2, 0, 1, 2}),
    MERCENARY(new int[]{3, 1, 1, 1}),
    ARTIFICER(new int[]{0, 1, 2, 2}),
    CHIEFTAIN(new int[]{0, 4, 1, 1}),
    ELDER(new int[]{0, 1, 1, 3});

    private final int[] primarySkillValues;

    }

