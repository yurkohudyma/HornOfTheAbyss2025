package ua.hudyma.domain.creatures.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ua.hudyma.domain.creatures.CreatureType;

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


