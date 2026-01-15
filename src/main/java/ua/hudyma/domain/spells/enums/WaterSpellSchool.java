package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.dto.HeroSkillSpellModifierDto;
import ua.hudyma.domain.spells.AbstractSpellSchool;

@Getter
@RequiredArgsConstructor
public enum WaterSpellSchool implements AbstractSpellSchool {
    ;

    private final Integer spellLevel;
    private final SpellAction spellAction;
    private final Integer manaCost;

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
    public HeroSkillSpellModifierDto getHeroSkillSpellModifierDto() {
        return null;
    }
}
