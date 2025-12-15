package ua.hudyma.enums;

import jakarta.persistence.criteria.CriteriaBuilder;

public record AttackResultDto(
        String attackingSlotId,
        String defendingSlotId,
        String attackerCreature,
        String defenderCreature,
        Integer attackDamage,
        Integer defenderHealth,
        Boolean defenderSurvivedAttack,
        Integer defenderHealthLeft,
        Boolean retaliationCommenced,
        Integer retaliatorDamage,
        Integer defendingFromRetaliatorHeath,
        Integer attackerHealthLeft,
        Boolean attackedSurvivedRetaliation

) {
}
