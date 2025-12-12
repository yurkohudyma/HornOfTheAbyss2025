package ua.hudyma.domain.towns.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ua.hudyma.domain.towns.enums.DwellingType;

import java.io.IOException;

public class DwellingTypeDeserializer extends JsonDeserializer<DwellingType> {
    @Override
    public DwellingType deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String code = p.getValueAsString();
        return DwellingTypeRegistry.fromCode(code);
    }
}
