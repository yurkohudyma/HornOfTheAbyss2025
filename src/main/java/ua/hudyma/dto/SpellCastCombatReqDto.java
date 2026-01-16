package ua.hudyma.dto;

import ua.hudyma.domain.spells.enums.SpellSchool;

public record SpellCastCombatReqDto(
        String attackerId,
        String defenderId,
        String defendingSlotId,
        String spell,
        SpellSchool spellSchool
) {
}
