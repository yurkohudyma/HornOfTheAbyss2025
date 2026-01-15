package ua.hudyma.dto;

public record SpellAttackResultDto(
        String defendingSlotId,
        String defenderCreature,
        Integer defenderCreatureCount,
        Integer survivedDefenderCreatureCount,
        Integer killedDefenderCreatureCount,
        Integer attackDamage,
        Integer defenderHealth,
        Boolean defenderSurvivedAttack,
        Integer defenderHealthLeft
) {
}
