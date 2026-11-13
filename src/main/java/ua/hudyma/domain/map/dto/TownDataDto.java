package ua.hudyma.domain.map.dto;

public record TownDataDto(
        int townIndex,
        String townName,
        MapCoordinatesDto mapCoordinatesDto
) {
}
