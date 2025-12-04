package ua.hudyma.domain.creatures;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CreatureTypeDeserializer extends JsonDeserializer<CreatureType> {

    @Override
    public CreatureType deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String code = p.getValueAsString();
        return CreatureTypeRegistry.fromCode(code);
    }
}

