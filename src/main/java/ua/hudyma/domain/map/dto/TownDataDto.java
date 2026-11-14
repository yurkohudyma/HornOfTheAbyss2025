package ua.hudyma.domain.map.dto;

import ua.hudyma.domain.map.enums.TownName;

public record TownDataDto(
        int townIndex,
        TownName townName,
        MapCoordinatesDto mapCoordinatesDto
) {
}
