package ua.hudyma.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public class FixedSizeListDeserializer extends JsonDeserializer<FixedSizeList<?>> {

    @Override
    public FixedSizeList<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        var rawList = p.readValueAs(List.class);
        var member = ctxt.getContextualType().getRawClass()
                .getDeclaredFields()[0];
        var annotation = member.getAnnotation(FixedSize.class);
        int maxSize = annotation != null ? annotation.value() : rawList.size();
        var fixed = new FixedSizeList<>(maxSize);
        if (rawList.size() > maxSize) {
            throw new IllegalStateException(
                    "List exceeds fixed size " + maxSize + ": got " + rawList.size()
            );
        }
        fixed.addAll(rawList);
        return fixed;
    }
}

