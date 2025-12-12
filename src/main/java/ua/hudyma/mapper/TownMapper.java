package ua.hudyma.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ua.hudyma.domain.towns.config.AbstractBuildingConfig;
import ua.hudyma.domain.towns.config.CastleBuildingConfig;
import ua.hudyma.domain.towns.config.InfernoBuildingConfig;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;
import ua.hudyma.service.PlayerService;

@Component
@RequiredArgsConstructor
@Log4j2
public class TownMapper extends BaseMapper<TownRespDto, Town, TownReqDto> {
    private final PlayerService playerService;

    @Override
    public TownRespDto toDto(Town town) {
        var townConfig = (CastleBuildingConfig) town.getBuildingConfig();
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
        var player = playerService
                .getPlayer(dto.ownerId());
        var town = new Town();
        town.setPlayer(player);
        town.setAlignment(computeAlignment(dto.faction()));
        town.setFaction(dto.faction());
        town.setName(dto.name());
        town.setDwellingTypeList(dto.dwellingTypeList());
        town.setGarrisonArmy(dto.garrisonArmy());
        town.setBuildingConfig(createConfigForFaction(dto.faction()));
        return town;
    }

    private static AbstractBuildingConfig createConfigForFaction(Faction f) {
        return switch (f) {
            case CASTLE -> new CastleBuildingConfig();
            case INFERNO -> new InfernoBuildingConfig();
            default -> throw new IllegalArgumentException("Unknown faction: " + f);
        };
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
