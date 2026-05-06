package ua.hudyma.domain.creatures.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;

import java.util.Map;

import static ua.hudyma.util.IdGenerator.generateId;

@Data
@NoArgsConstructor
public class CreatureSlot {
    private String slotId = generateId(1,3);
    private CreatureType type;
    private Integer quantity;
    private Map<ModifiableSkill, ModifiableData> modifiableDataMap;

    public CreatureSlot(CreatureType creatureType, int quantity) {
        this.type = creatureType;
        this.quantity = quantity;
    }

}
