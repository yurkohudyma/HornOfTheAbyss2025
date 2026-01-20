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
    PRECISION(2, SpellAction.BUF, 16, //real is 8
            null, 1, List.of()),
    VISIONS(2, SpellAction.MISC, 0, null, 1, List.of()),
    SUMMON_AIR_ELEMENTAL(5,SUMMON, 25, POWER,
            1,
            List.of(2f, 2.5f, 3f));


    private final int spellLevel;
    private final SpellAction spellAction;
    private final Integer manaCost;
    private final PrimarySkill spellPrimarySkill; // hero's primary skill accounted in spell damage calculation (mostly WISDOM)
    private final Integer modifierCoefficient;
    private final List<Float> modifiedValuesList;


    /*private static HeroSkillSpellModifierDto toDto(
            PrimarySkill skill,
            Integer coefficient,
            List<Integer> modifList) {
        return new HeroSkillSpellModifierDto(
                skill,
                coefficient,
                toSkillLevelMap(modifList));
    }*/

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
