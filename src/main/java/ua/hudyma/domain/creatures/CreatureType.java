package ua.hudyma.domain.creatures;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CreatureTypeDeserializer.class)
public interface CreatureType {
    String getCode();
}
