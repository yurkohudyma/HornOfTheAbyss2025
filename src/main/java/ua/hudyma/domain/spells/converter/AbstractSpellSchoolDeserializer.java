package ua.hudyma.domain.spells.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import java.io.IOException;

public class AbstractSpellSchoolDeserializer extends
        JsonDeserializer<AbstractSpellSchool> {
    @Override
    public AbstractSpellSchool deserialize(
            JsonParser jsonParser,
            DeserializationContext ctx) throws IOException {
        var code = jsonParser.getValueAsString();
        return SpellRegistry.fromCode(code);
    }
}
