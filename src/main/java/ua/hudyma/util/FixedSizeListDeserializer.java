package ua.hudyma.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.List;

public class FixedSizeListDeserializer extends JsonDeserializer<FixedSizeList<?>>
        implements ContextualDeserializer {
    private final int maxSize;

    public FixedSizeListDeserializer(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        FixedSize annotation = property.getAnnotation(FixedSize.class);
        int size = annotation != null ? annotation.value() : 5;
        return new FixedSizeListDeserializer(size);
    }

    @Override
    public FixedSizeList<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        var rawList = p.readValueAs(List.class);
        if (rawList.size() > maxSize) {
            throw new IllegalStateException(
                    "List exceeds fixed size " + maxSize + ": got " + rawList.size()
            );
        }
        var fixed = new FixedSizeList<>(maxSize);
        fixed.addAll(rawList);
        return fixed;
    }
}

/* public class FixedSizeListDeserializer extends JsonDeserializer<FixedSizeList<?>> {

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
    }*/

