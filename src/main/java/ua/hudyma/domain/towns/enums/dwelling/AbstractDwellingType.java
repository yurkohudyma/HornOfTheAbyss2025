package ua.hudyma.domain.towns.enums.dwelling;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.towns.converter.AbstractDwellingTypeDeserializer;

import java.util.Set;

@JsonDeserialize(
        using = AbstractDwellingTypeDeserializer.class)
public interface AbstractDwellingType {
    String getCode();
    CreatureType getCreature();
    CreatureType getEssentialCreature();
    Set<CreatureType> getCreatureSet();
}
