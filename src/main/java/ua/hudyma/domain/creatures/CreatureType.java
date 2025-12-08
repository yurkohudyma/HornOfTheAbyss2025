package ua.hudyma.domain.creatures;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(
        using = CreatureTypeDeserializer.class,
        keyUsing = CreatureTypeKeyDeserializer.class
)
public interface CreatureType {
    String getCode();
    Integer getLevel();
}
