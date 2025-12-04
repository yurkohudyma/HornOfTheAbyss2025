package ua.hudyma.domain.creatures.dto;

import ua.hudyma.domain.creatures.enums.CreaturePropertyType;
import ua.hudyma.domain.creatures.enums.PropertyUnit;

public record CreaturePropertyValue(
        Object property, //ARCHDEVIL
        CreaturePropertyType type, //STRING
        Integer value, //150
        PropertyUnit unit // PERCENT
) {
}
