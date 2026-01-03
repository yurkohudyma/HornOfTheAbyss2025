package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import static ua.hudyma.domain.spells.enums.SpellAction.DAMAGE;

@Getter
@RequiredArgsConstructor
public enum FireSpellSchool implements AbstractSpellSchool {
    ARMAGEDDON(4, DAMAGE, 24),
    //BASIC :All troops take ((Power x 50) + 30) points of damage.
    //ADV: All troops take ((Power x 50) + 60) points of damage.
    //EXP: All troops take ((Power x 50) + 120) points of damage.
    SLAYER (4, SpellAction.BUF, 16)
    //BASIC : One friendly target unit's attack rating is increased by eight against behemoths, dragons, and hydras.
    //ADV: Same as Basic effect, except that attack bonus also affects devils and angels.
    //EXP: Same as Advanced effect, except attack bonus also affects titans.
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
    public Integer getManaCost(){
        return manaCost;
    }
}
