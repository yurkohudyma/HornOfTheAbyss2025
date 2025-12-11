package ua.hudyma.mapper;

import org.springframework.stereotype.Component;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;

import java.util.List;

@Component
public class TownMapper extends BaseMapper<TownRespDto, Town, TownReqDto> {
    @Override
    public TownRespDto toDto(Town town) {
        var townConfig = (CastleBuildingConfig) town.getBuildingConfig();
        townConfig.setHall(HallType.CAPITOL);
        var commonBuildingList = townConfig.commonBuildingList;
        commonBuildingList.add(CommonBuildingType.BLACKSMITH);
        townConfig.setFortification(FortificationType.NONE);
        townConfig.setShipyard(Shipyard.NONE);
        return new TownRespDto();
    }

    @Override
    public Town toEntity(TownReqDto dto) {
        var town = new Town();
        town.setAlignment(Alignment.GOOD);
        town.setFaction(Faction.CASTLE);

        return town;
    }
}
