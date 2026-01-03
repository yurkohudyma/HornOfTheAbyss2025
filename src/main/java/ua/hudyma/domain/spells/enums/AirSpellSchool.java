package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.spells.AbstractSpellSchool;

@Getter
@RequiredArgsConstructor
public enum AirSpellSchool implements AbstractSpellSchool {
    DISGUISE(2, SpellAction.MISC, 0),
    PRECISION(2, SpellAction.BUF, 16), //real is 8
    VISIONS(2, SpellAction.MISC, 0)

    ;

    private final int spellLevel;
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
    public Integer getManaCost() {
        return manaCost;
    }
}
