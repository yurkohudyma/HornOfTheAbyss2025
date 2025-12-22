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
import ua.hudyma.domain.towns.dto.DwellReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.dwelling.CastleDwellingType;
import ua.hudyma.domain.towns.enums.properties.*;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.resource.ResourceDemandRespDto;
import ua.hudyma.service.PlayerService;
import ua.hudyma.service.TownService;

import java.util.Arrays;

import static ua.hudyma.domain.towns.converter.BuildingTypeResolver.resolve;
import static ua.hudyma.domain.towns.converter.BuildingTypeResolver.resolveDwellingType;
import static ua.hudyma.domain.towns.enums.CommonBuildingType.MAGE_GUILD;

@Service
@RequiredArgsConstructor
@Log4j2
public class AbstractBuildService {
    private final TownService townService;
    private final PlayerService playerService;
    private final CommonBuildService commonBuildService;
    private final TownMapper townMapper;
    private final DwellingBuildService dwellingBuildService;

    public ResourceDemandRespDto getResourceDemand(String type, Integer level) {
        var enumClass = resolveBuildingEnumType(type);
        var modifiedType = getModifiedPropertiesName(type, level);
        var constantProps =
                getTypeSpecificConstantProperties(modifiedType, enumClass);
        return townMapper.mapToResourceDto(constantProps);
    }

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
        var buildingType = resolve(dto.buildingType());
        var modifiedPropertiesName = getModifiedPropertiesName(buildingType, buildingLevel);
        var enumTypeClass = resolveBuildingEnumType(
                buildingType.toString());
        var constantProperties =
                getTypeSpecificConstantProperties(
                        modifiedPropertiesName, enumTypeClass);
        resolveBuildingTypeAndInvokeSpecificFactoryService
                (player, buildingLevel, town, buildingType, constantProperties);
        var msg = buildingLevel > 0 ? String.format("%s Level %d has been erected in %s",
                buildingType, buildingLevel, town.getName()) :
                String.format("%s has been erected in %s",
                        buildingType, town.getName());
        log.info(msg);
        return msg;
    }

    @Transactional
    public String buildDwelling (AbstractBuildReqDto dto){
        var player = playerService.getPlayer(dto.playerId());
        var buildingLevel = dto.buildingLevel();
        if (buildingLevel > 5) {
            throw new IllegalArgumentException
                    ("Building LEVEL is limited by 5, while provided = " + buildingLevel);
        }
        var town = townService.getTown(dto.name());
        checkTownBelongsToPlayer(player, town);
        var buildingType = resolveDwellingType(dto.buildingType());
        var enumTypeClass = resolveDwellingEnumType
                (buildingType.toString());
        var constantProperties =
                getTypeSpecificDwellConstantProperties(
                        buildingType.toString(), enumTypeClass);
        resolveDwellingTypeAndInvokeSpecificFactoryService
                (player, buildingLevel, town, buildingType, constantProperties);
        return "";
    }

    AbstractDwellingTypeProperties getTypeSpecificDwellConstantProperties(
            String propertyName, Class<? extends AbstractDwellingType> enumTypeClass) {
        if (enumTypeClass.equals(CastleDwellingType.class)) {
            return CastleDwellingTypeProperties.valueOf(propertyName);
        }
        else throw new IllegalArgumentException("Unknown AbstractBuildingType propertyName: "
                    + propertyName);
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
        var buildingType = resolve(type);
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

    public Class<? extends AbstractDwellingType> resolveDwellingEnumType(String type) {
        var buildingType = resolveDwellingType(type);
        for (Class<? extends AbstractDwellingType> ttype : BuildingTypeResolver.ENUM_DWELL_TYPES) {
            var enumConstants = ttype.getEnumConstants();
            var typeList = Arrays.asList(enumConstants);
            if (typeList.contains(buildingType)) {
                return ttype;
            }
        }
        throw new IllegalArgumentException("No match for AbstractDwellingType propertyName: "
                + type);
    }

    private void resolveDwellingTypeAndInvokeSpecificFactoryService(
            Player player,
            int buildingLevel,
            Town town,
            AbstractDwellingType buildingType,
            AbstractDwellingTypeProperties constantProperties) {
        if (buildingType instanceof CastleDwellingType) {
            dwellingBuildService.build(new DwellReqDto(
                    town,
                    buildingType,
                    buildingLevel,
                    player,
                    constantProperties));
        }
    }

    private void resolveBuildingTypeAndInvokeSpecificFactoryService(
            Player player,
            int buildingLevel,
            Town town,
            AbstractBuildingType buildingType,
            AbstractBuildingTypeProperties constantProperties) {
        if (    buildingType instanceof CommonBuildingType ||
                buildingType instanceof HallType ||
                buildingType instanceof FortificationType ||
                buildingType instanceof UniqueBuildingType ||
                buildingType instanceof HordeBuildingType){
            commonBuildService.build(new BuildReqDto(
                    town,
                    buildingType,
                    buildingLevel,
                    player,
                    constantProperties));
        }
        else if (buildingType instanceof AbstractDwellingType){
            throw new IllegalArgumentException("AbstractDwellingType not APPREHENDED");
        }
        else if (buildingType instanceof GrailBuildingType){
            throw new IllegalArgumentException("GrailBuildingType not APPREHENDED");
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

    private String getModifiedPropertiesName(String buildingType, int buildingLevel) {
        if (buildingType.equals(MAGE_GUILD.name())) return buildingType + "_L" + buildingLevel;
        return buildingType;
    }
}
