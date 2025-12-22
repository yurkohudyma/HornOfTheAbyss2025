package ua.hudyma.domain.towns.dto;

import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.enums.AbstractBuildingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.properties.AbstractBuildingTypeProperties;

public record DwellReqDto(
        Town town,
        AbstractDwellingType buildingType,
        int buildingLevel,
        Player player,
        AbstractDwellingTypeProperties constantProperties) {
}
