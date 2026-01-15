package ua.hudyma.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record SpellCastCombatReqDto(
        String attackerId,
        String defenderId,
        String attackingSlotId,
        String defendingSlotId,
        String spell
) {
}
