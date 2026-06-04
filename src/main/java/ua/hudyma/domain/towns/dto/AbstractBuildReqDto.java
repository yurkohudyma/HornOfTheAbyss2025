package ua.hudyma.domain.towns.dto;

public record AbstractBuildReqDto(
        String townName,
        String buildingType,
        int buildingLevel
        ) {}
