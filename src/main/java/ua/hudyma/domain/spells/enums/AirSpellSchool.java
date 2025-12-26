package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.spells.AbstractSpellSchool;

@Getter
@RequiredArgsConstructor
public enum AirSpellSchool implements AbstractSpellSchool {
    DISGUISE(2, SpellAction.MISC),
    PRECISION(2, SpellAction.BUF),
    VISIONS(2, SpellAction.MISC)

    ;

    private final int spellLevel;
    private final SpellAction spellAction;

    @Override
    public int getSpellLevel() {
        return spellLevel;
    }
}
