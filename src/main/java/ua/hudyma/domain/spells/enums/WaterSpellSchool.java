package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import java.util.List;

import static java.lang.Float.MAX_VALUE;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.POWER;
import static ua.hudyma.domain.spells.enums.SpellAction.*;

@Getter
@RequiredArgsConstructor
public enum WaterSpellSchool implements AbstractSpellSchool {

MAGIC_ARROW (1, DAMAGE, 5, POWER, 10, List.of(10f, 20f, 30f)),
ICE_BOLT (2, DAMAGE, 0, POWER, 20, List.of(10f, 20f, 50f)), // modified value list element + (power x modifier coeff)
FROST_RING (3, DAMAGE, 0, POWER, 10, List.of(15f, 30f, 60f)),
BLESS (1, BUF, 0, POWER, 1, List.of(0f, 1f, 1f)),
PROTECTION_FROM_WATER (1, BUF, 0, null, 1, List.of(50f, 75f, 75f)),
MIRTH (3, BUF, 0, POWER, 1, List.of(1f, 2f, MAX_VALUE)),
PRAYER (4, BUF, 0, POWER, 1, List.of(2f, 4f, 4f)),
WEAKNESS (2, DEBUF, 0, POWER, 1, List.of(3f, 6f, 6f)),
FORGETFULNESS (3, DEBUF, 0, POWER, 1, List.of(50f, 100f, MAX_VALUE)),
CURE (1, BUF, 0, POWER, 5, List.of(10f, 20f, 30f)),
DISPEL (1, BUF, 0, null, 1, List.of(1f, MAX_VALUE, Float.POSITIVE_INFINITY)),
REMOVE_OBSTACLE (2, MISC, 0, null, 1, List.of()),
TELEPORT (3, MISC, 0, null, 1, List.of()),
CLONE (4, MISC, 0, POWER, 1, List.of(5f, 6f, 7f)),
SUMMON_WATER_ELEMENTAL (5, SUMMON, 0, POWER, 1, List.of(2f, 2.5f, 3f)),
SUMMON_BOAT (1, ADVENTURE, 0, null, 1, List.of(50f, 75f, 100f)),
SCUTTLE_BOAT (2, ADVENTURE, 0, null, 1, List.of(50f, 75f, 100f)),
VISIONS (2, ADVENTURE, 0, POWER, 1, List.of(1f, 2f, 3f)), //basic: Range is equal to power or three, whichever is greater.
WATER_WALK (4, MISC, 0, null, 1, List.of(40f, 20f, 1f));

    private final Integer spellLevel;
    private final SpellAction spellAction;
    private final Integer manaCost;
    private final PrimarySkill spellPrimarySkill; // hero's primary skill accounted in spell damage calculation (mostly POWER)
    private final Integer modifierCoefficient;
    private final List<Float> modifiedValuesList;

    @Override
    public int getSpellLevel() {
        return spellLevel;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public SpellAction getSpellAction() {
        return spellAction;
    }

    @Override
    public Integer getManaCost() {
        return manaCost;
    }

    @Override
    public PrimarySkill getSpellPrimarySkill() {
        return spellPrimarySkill;
    }

    @Override
    public Integer getModifierCoefficient() {
        return modifierCoefficient;
    }

    @Override
    public List<Float> getModifiedValuesList() {
        return modifiedValuesList;
    }
}
