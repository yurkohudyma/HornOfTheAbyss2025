package ua.hudyma.dto;

public record AttackResultDto(
        String attackingSlotId,
        String defendingSlotId,
        String attackerCreature,
        String defenderCreature,
        Integer attackerCreatureCount,
        Integer defenderCreatureCount,
        Integer survivedAttackerCreatureCount,
        Integer survivedDefenderCreatureCount,
        Integer killedAttackerCreatureCount,
        Integer killedDefenderCreatureCount,
        Integer attackDamage,
        Integer defenderHealth,
        Boolean defenderSurvivedAttack,
        Integer defenderHealthLeft,
        Boolean retaliationCommenced,
        Integer retaliatorDamage,
        Integer attackerHealthLeft,
        Boolean attackedSurvivedRetaliation

) {
}
