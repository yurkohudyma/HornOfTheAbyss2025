package ua.hudyma.domain.creatures;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.creatures.converter.CreatureTypeDeserializer;
import ua.hudyma.domain.creatures.converter.CreatureTypeKeyDeserializer;

@JsonDeserialize(
        using = CreatureTypeDeserializer.class,
        keyUsing = CreatureTypeKeyDeserializer.class
)
public interface CreatureType {
    String getCode();
    Integer getLevel();
}
