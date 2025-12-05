package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.creatures.dto.CreatureSlot;

import java.util.List;

public record ReinforceReqDto(
        String heroCode,
        List<CreatureSlot> armyList
) {}
