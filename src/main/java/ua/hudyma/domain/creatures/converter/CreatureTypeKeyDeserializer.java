package ua.hudyma.domain.creatures.converter;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import ua.hudyma.domain.creatures.CreatureType;

import java.io.IOException;

public class CreatureTypeKeyDeserializer extends KeyDeserializer {

    @Override
    public CreatureType deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return CreatureTypeRegistry.fromCode(key);
    }
}

