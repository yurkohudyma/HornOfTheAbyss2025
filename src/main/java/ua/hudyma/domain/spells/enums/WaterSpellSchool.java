package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.dto.HeroSkillSpellModifierDto;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum WaterSpellSchool implements AbstractSpellSchool {
    ;

    private final Integer spellLevel;
    private final SpellAction spellAction;
    private final Integer manaCost;
    private final PrimarySkill spellPrimarySkill; // hero's primary skill accounted in spell damage calculation (mostly WISDOM)
    private final Integer modifierCoefficient;
    private final List<Integer> modifiedValuesList;

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
    public List<Integer> getModifiedValuesList() {
        return modifiedValuesList;
    }
}
