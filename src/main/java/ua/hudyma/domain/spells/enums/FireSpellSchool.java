package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.spells.AbstractSpellSchool;

@Getter
@RequiredArgsConstructor
public enum FireSpellSchool implements AbstractSpellSchool {
    SLAYER (4, SpellAction.BUF)
    //BASIC : One friendly target unit's attack rating is increased by eight against behemoths, dragons, and hydras.
    //ADV: Same as Basic effect, except that attack bonus also affects devils and angels.
    //EXP: Same as Advanced effect, except attack bonus also affects titans.
    ;

    private final Integer spellLevel;
    private final SpellAction spellAction;

    @Override
    public int getSpellLevel() {
        return spellLevel;
    }

    @Override
    public String getName() {
        return name();
    }
}
