package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.towns.enums.CommonBuildingType;

public record BuildReqDto(
        Long playerId,
        String name,
        CommonBuildingType buildingType,
        Integer buildingLevel
        ) {}
