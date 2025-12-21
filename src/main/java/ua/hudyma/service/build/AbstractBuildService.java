package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.converter.BuildingTypeResolver;
import ua.hudyma.domain.towns.dto.AbstractBuildReqDto;
import ua.hudyma.domain.towns.dto.BuildReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.domain.towns.enums.properties.*;
import ua.hudyma.service.PlayerService;
import ua.hudyma.service.TownService;

import java.util.Arrays;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.MAGE_GUILD;

@Service
@RequiredArgsConstructor
@Log4j2
public class AbstractBuildService {
    private final TownService townService;
    private final PlayerService playerService;
    private final CommonBuildService commonBuildService;

    @Transactional
    public String build(AbstractBuildReqDto dto) {
        var player = playerService.getPlayer(dto.playerId());
        var buildingLevel = dto.buildingLevel();
        if (buildingLevel > 5) {
            throw new IllegalArgumentException
                    ("Building LEVEL is limited by 5, while provided = " + buildingLevel);
        }
        var town = townService.getTown(dto.name());
        checkTownBelongsToPlayer(player, town);
        var buildingType = BuildingTypeResolver.resolve(dto.buildingType());
        var modifiedPropertiesName = getModifiedPropertiesName(buildingType, buildingLevel);
        var enumTypeClass = resolveBuildingEnumType(
                buildingType.toString());
        var constantProperties =
                getTypeSpecificConstantProperties(
                        modifiedPropertiesName, enumTypeClass);
        resolveBuildingTypeAndInvokeSpecificFactoryService
                (player, buildingLevel, town, buildingType, constantProperties);
        var msg = String.format("%s Level %d has been erected in %s",
                buildingType, buildingLevel, town.getName());
        log.info(msg);
        return msg;
    }

    private void resolveBuildingTypeAndInvokeSpecificFactoryService(
            Player player,
            int buildingLevel,
            Town town,
            AbstractBuildingType buildingType,
            AbstractBuildingTypeProperties constantProperties) {
        if (buildingType instanceof CommonBuildingType){
            commonBuildService.build(new BuildReqDto(
                    town,
                    buildingType,
                    buildingLevel,
                    player,
                    constantProperties));
        }
        else if (buildingType instanceof HallType){
            throw new IllegalArgumentException("Hall type not APPREHENDED");
        }
    }

    private static void checkTownBelongsToPlayer(Player player, Town town) {
        if (!player.getTownsList().contains(town)) {
            throw new IllegalStateException(town.getName() + " does NOT belong to " + player.getName());
        }
    }

    private String getModifiedPropertiesName(AbstractBuildingType buildingType, int buildingLevel) {
        if (buildingType == MAGE_GUILD) return buildingType + "_L" + buildingLevel;
        return buildingType.toString();
    }

    AbstractBuildingTypeProperties getTypeSpecificConstantProperties(
            String propertyName, Class<? extends AbstractBuildingType> enumTypeClass) {
        if (enumTypeClass.equals(CommonBuildingType.class)) {
            return CommonBuildingTypeProperties.valueOf(propertyName);
        }
        else if (enumTypeClass.equals(FortificationType.class)) {
            return FortificationTypeProperties.valueOf(propertyName);
        }
        else if (enumTypeClass.equals(HallType.class)){
            return HallTypeProperties.valueOf(propertyName);
        }
        else if (enumTypeClass.equals(HordeBuildingType.class)){
            return HordeBuildingTypeProperties.valueOf(propertyName);
        }
        else if (enumTypeClass.equals(UniqueBuildingType.class)){
            return UniqueBuildingTypeProperties.valueOf(propertyName);
        }
        else throw new IllegalArgumentException("Unknown AbstractBuildingType propertyName: "
                    + propertyName);
    }

    public Class<? extends AbstractBuildingType> resolveBuildingEnumType(String type) {
        var buildingType = BuildingTypeResolver.resolve(type);
        for (Class<? extends AbstractBuildingType> ttype : BuildingTypeResolver.ENUM_TYPES) {
            var enumConstants = ttype.getEnumConstants();
            var typeList = Arrays.asList(enumConstants);
            if (typeList.contains(buildingType)) {
                return ttype;
            }
        }
        throw new IllegalArgumentException("No match for AbstractBuildingType propertyName: "
                + type);
    }
}
