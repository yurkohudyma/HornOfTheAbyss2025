package ua.hudyma.domain.towns.enums.dwelling;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.towns.converter.AbstractDwellingTypeDeserializer;

@JsonDeserialize(
        using = AbstractDwellingTypeDeserializer.class)
public interface AbstractDwellingType {
    String getCode();
    CreatureType getCreature();
}
