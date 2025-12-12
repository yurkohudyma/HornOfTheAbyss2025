package ua.hudyma.domain.towns.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.towns.converter.DwellingTypeDeserializer;
import ua.hudyma.domain.towns.converter.DwellingTypeKeyDeserializer;

@JsonDeserialize(
        using = DwellingTypeDeserializer.class,
        keyUsing = DwellingTypeKeyDeserializer.class)
public interface DwellingType {
    String getCode();
    //String getCreature();
}
