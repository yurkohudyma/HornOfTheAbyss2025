package ua.hudyma.domain.spells;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.spells.converter.AbstractSpellSchoolDeserializer;
import ua.hudyma.domain.spells.enums.SpellAction;

@JsonDeserialize(
        using = AbstractSpellSchoolDeserializer.class)
public interface AbstractSpellSchool {
    int getSpellLevel();
    String getName();
    SpellAction getSpellAction();
    Integer getManaCost();
}
