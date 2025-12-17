package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.towns.enums.properties.AbstractBuildingTypeProperties;

public record BuildReqDto(
        String name,
        AbstractBuildingTypeProperties buildingType
        ) {}
