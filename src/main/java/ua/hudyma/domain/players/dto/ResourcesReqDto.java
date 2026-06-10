package ua.hudyma.domain.players.dto;

import ua.hudyma.domain.resource.enums.ResourceType;

import java.util.Map;

public record ResourcesReqDto(
        Long playerId,
        Map<ResourceType, Integer> resourceMap) {
}
