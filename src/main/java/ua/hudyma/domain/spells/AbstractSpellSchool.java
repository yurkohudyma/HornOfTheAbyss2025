package ua.hudyma.domain.spells;

import ua.hudyma.domain.spells.enums.SpellAction;

public interface AbstractSpellSchool {
    int getSpellLevel();
    String getName();
    SpellAction getSpellAction();
    Integer getManaCost();
}
