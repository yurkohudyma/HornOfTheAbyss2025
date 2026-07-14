package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import java.util.List;

import static ua.hudyma.domain.heroes.enums.PrimarySkill.POWER;
import static ua.hudyma.domain.spells.enums.SpellAction.SUMMON;

@Getter
@RequiredArgsConstructor
public enum AirSpellSchool implements AbstractSpellSchool {
    MAGIC_ARROW(1,
            SpellAction.DAMAGE,
            6,
            POWER,
            10,
            List.of(10f, 20f, 30f)),
    DISGUISE(2, SpellAction.MISC, 0,
            null,
            1,
            List.of()),
    LIGHTING_BOLT(2, SpellAction.DAMAGE, 10, POWER, 25, List.of(10f, 20f, 50f)),
    PRECISION(2, SpellAction.BUF, 16, //real is 8
            null, 1, List.of()),
    VISIONS(2, SpellAction.MISC, 0, null, 1, List.of()),
    /** Chain Lightning strikes up to four or five creature stacks
     * causing full damage for the initial target, and halving
     * for each target after that. The closest creature stack
     * to the initial target becomes the second target of the
     * spell whether it is a friend or a foe, and this method
     * repeats itself for all the spell's targets.
     * However, the same creature stack cannot be targeted twice.
     * If two or more targets are at the equal distance from previous target,
     * then the stack is randomly chosen. If the spell is resisted after
     * it has already struck at least one target, it will continue
     * arcing to the next target.         *
     *     This spell deals up to (46 + power x 75) damage on basic level,
     *     (96 + power x 77.5) on advanced level and (193 + power x 77.5)
     *     with expert air magic.
     */
    CHAIN_LIGHTNING (4, SpellAction.DAMAGE, 24, POWER, 40, List.of(25f, 50f, 100f)),

    SUMMON_AIR_ELEMENTAL(5, SUMMON, 25, POWER,
            1,
            List.of(2f, 2.5f, 3f));

    private final int spellLevel;

    private final SpellAction spellAction;

    private final Integer manaCost;

    private final PrimarySkill spellPrimarySkill;

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
