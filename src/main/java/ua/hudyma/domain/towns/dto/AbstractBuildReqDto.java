package ua.hudyma.domain.towns.dto;

public record AbstractBuildReqDto(
        Long playerId,
        String townName,
        String buildingType,
        int buildingLevel
        ) {}
