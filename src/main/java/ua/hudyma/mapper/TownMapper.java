package ua.hudyma.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.domain.towns.enums.properties.AbstractBuildingTypeProperties;
import ua.hudyma.domain.towns.enums.properties.CommonBuildingTypeProperties;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;
import ua.hudyma.resource.ResourceDemandRespDto;
import ua.hudyma.service.PlayerService;

@Component
@RequiredArgsConstructor
@Log4j2
public class TownMapper extends BaseMapper<TownRespDto, Town, TownReqDto> {
    private final PlayerService playerService;

    public ResourceDemandRespDto mapToResourceDto(
            AbstractBuildingTypeProperties constantProps) {
        return new ResourceDemandRespDto(
                ((CommonBuildingTypeProperties) constantProps).getRequiredBuiltBuildings(),
                ((CommonBuildingTypeProperties) constantProps).getRequiredResourceMap(),
                ((CommonBuildingTypeProperties) constantProps).getExcludedFactions());
    }

    @Override
    public TownRespDto toDto(Town town) {
        var visitingHero = town.getVisitingHero();
        var garrisonHero = town.getGarrisonHero();
        return new TownRespDto(
                town.getPlayer().getName(),
                town.getName(),
                town.getAlignment(),
                town.getFaction(),
                visitingHero != null ? visitingHero.getName() : "NA",
                garrisonHero != null ? garrisonHero.getName() : "NA",
                town.getDwellingTypeList(),
                town.getGarrisonArmy(),
                town.getCommonBuildingMap(),
                town.getHallType()
        );
    }

    @Override
    public Town toEntity(TownReqDto dto) {
        var player = playerService
                .getPlayer(dto.ownerId());
        var town = new Town();
        town.setPlayer(player);
        town.setAlignment(computeAlignment(dto.faction()));
        town.setFaction(dto.faction());
        town.setName(dto.name());
        town.setDwellingTypeList(dto.dwellingTypeList());
        town.setGarrisonArmy(dto.garrisonArmy());
        return town;
    }

    private static Alignment computeAlignment(Faction faction) {
        var constraintArray = Alignment.values();
        for (Alignment alignment : constraintArray){
            if (alignment.getFaction().contains(faction)){
                return alignment;
            }
        }
        throw new IllegalArgumentException("No Alignment bound with " +
                "any Faction");
    }
}
