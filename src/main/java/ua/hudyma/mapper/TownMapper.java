package ua.hudyma.mapper;

import org.springframework.stereotype.Component;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;

@Component
public class TownMapper extends BaseMapper<TownRespDto, Town, TownReqDto> {
    @Override
    public TownRespDto toDto(Town town) {
        var townConfig = (CastleBuildingConfig) town.getBuildingConfig();
        var commonBuildingList = townConfig
                .commonBuildingList;
        commonBuildingList.add(CommonBuildingType.BLACKSMITH);
        return new TownRespDto(
                town.getPlayer().getName(),
                town.getName(),
                town.getAlignment(),
                town.getFaction(),
                town.getDwellingTypeList(),
                town.getGarrisonArmy(),
                townConfig.commonBuildingList,
                townConfig.getInitialConstantList()
        );
    }

    @Override
    public Town toEntity(TownReqDto dto) {
        var town = new Town();
        town.setAlignment(Alignment.GOOD);
        town.setFaction(Faction.CASTLE);

        return town;
    }
}
