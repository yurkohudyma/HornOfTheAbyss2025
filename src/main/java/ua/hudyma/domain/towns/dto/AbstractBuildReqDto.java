package ua.hudyma.domain.towns.dto;

public record AbstractBuildReqDto(
        Long playerId,
        String name,
        String buildingType,
        int buildingLevel
        ) {}
