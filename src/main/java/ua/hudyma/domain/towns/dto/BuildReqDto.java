package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.towns.enums.properties.CommonBuildingTypeProperties;

public record BuildReqDto(
        Long playerId,
        String name,
        CommonBuildingTypeProperties buildingType
        ) {}
