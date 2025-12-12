package ua.hudyma.domain.towns.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.towns.converter.DwellingTypeDeserializer;

@JsonDeserialize(
        using = DwellingTypeDeserializer.class)
public interface DwellingType {
    String getCode();
    String getCreature();
}
