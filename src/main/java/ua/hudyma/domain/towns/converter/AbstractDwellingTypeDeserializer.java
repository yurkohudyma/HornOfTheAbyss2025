package ua.hudyma.domain.towns.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;

import java.io.IOException;

public class AbstractDwellingTypeDeserializer extends JsonDeserializer<AbstractDwellingType> {
    @Override
    public AbstractDwellingType deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String code = p.getValueAsString();
        return AbstractDwellingTypeRegistry.fromCode(code);
    }
}
