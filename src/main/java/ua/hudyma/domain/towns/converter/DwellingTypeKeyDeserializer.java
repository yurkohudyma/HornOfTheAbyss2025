package ua.hudyma.domain.towns.converter;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import ua.hudyma.domain.towns.enums.DwellingType;

import java.io.IOException;

public class DwellingTypeKeyDeserializer extends KeyDeserializer {
    @Override
    public DwellingType deserializeKey(String code, DeserializationContext deserializationContext) throws IOException {
        return DwellingTypeRegistry.fromCode(code);
    }
}
