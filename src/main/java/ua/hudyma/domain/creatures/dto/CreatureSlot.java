package ua.hudyma.domain.creatures.dto;

import lombok.Data;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;

import java.util.Map;

import static ua.hudyma.util.IdGenerator.generateId;

@Data
public class CreatureSlot {
    private String slotId = generateId(1,3);
    private CreatureType type;
    private Integer quantity;
    private Map<ModifiableSkill, ModifiableData> modifiableDataMap;
}
