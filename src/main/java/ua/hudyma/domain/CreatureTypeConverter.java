package ua.hudyma.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.CreatureTypeRegistry;

@Converter(autoApply = true)
public class CreatureTypeConverter implements AttributeConverter<CreatureType, String> {
    @Override
    public String convertToDatabaseColumn(CreatureType attribute) {
        return attribute == null ? null : attribute.getCode();
    }
    @Override
    public CreatureType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CreatureTypeRegistry.fromCode(dbData);
    }
}


