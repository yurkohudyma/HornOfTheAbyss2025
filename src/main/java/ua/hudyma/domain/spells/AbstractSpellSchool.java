package ua.hudyma.domain.spells;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.converter.AbstractSpellSchoolDeserializer;
import ua.hudyma.domain.spells.enums.SpellAction;

import java.util.List;

@JsonDeserialize(
        using = AbstractSpellSchoolDeserializer.class)
public interface AbstractSpellSchool {
    int getSpellLevel();
    String getName();
    SpellAction getSpellAction();
    Integer getManaCost();
    PrimarySkill getSpellPrimarySkill();
    Integer getModifierCoefficient();
    List<Integer> getModifiedValuesList();
}
