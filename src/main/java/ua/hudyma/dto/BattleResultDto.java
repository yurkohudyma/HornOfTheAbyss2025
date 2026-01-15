package ua.hudyma.dto;

import ua.hudyma.domain.creatures.dto.CreatureSlot;

import java.util.List;

public record BattleResultDto(
        Boolean attackerWonBattle,
        Boolean defenderSurvivedBattle,
        List<CreatureSlot> attackerSurvivedSlotList,
        List<CreatureSlot> defenderSurvivedSlotList
) {}
