package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.enums.AbstractBuildingType;
import ua.hudyma.domain.towns.enums.properties.AbstractBuildingTypeProperties;

public record BuildReqDto(
        Town town,
        AbstractBuildingType buildingType,
        int buildingLevel,
        Player player,
        AbstractBuildingTypeProperties constantProperties) {
}
