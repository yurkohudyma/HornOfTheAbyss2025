package ua.hudyma.domain.creatures;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ua.hudyma.domain.creatures.converter.CreatureTypeDeserializer;
import ua.hudyma.domain.creatures.converter.CreatureTypeKeyDeserializer;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;

@JsonDeserialize(
        using = CreatureTypeDeserializer.class,
        keyUsing = CreatureTypeKeyDeserializer.class
)
public interface CreatureType {
    String getCode();
    Integer getLevel();
    EnumMap<ResourceType, Integer> getRequiredResourceMap();
}
