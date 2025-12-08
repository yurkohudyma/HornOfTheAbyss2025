package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.creatures.CreatureType;

public record CreatureSlotRespDto(
        String slotId,
        CreatureType type,
        Integer quantity
) {
}
