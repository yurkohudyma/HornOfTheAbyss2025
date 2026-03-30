package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.AbstractBuildReqDto;
import ua.hudyma.domain.towns.dto.BuildReqDto;
import ua.hudyma.domain.towns.dto.DwellReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.dwelling.CastleDwellingType;
import ua.hudyma.domain.towns.enums.dwelling.RampartDwellingType;
import ua.hudyma.domain.towns.enums.properties.*;
import ua.hudyma.domain.towns.enums.properties.dwelling.CastleDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.properties.dwelling.RampartDwellingTypeProperties;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.RequiredBuildingMissingException;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.resource.ResourceDemandRespDto;
import ua.hudyma.service.PlayerService;
import ua.hudyma.service.TownService;

import java.util.Arrays;
import java.util.Map;

import static ua.hudyma.domain.towns.converter.BuildingTypeResolver.*;
import static ua.hudyma.domain.towns.enums.CommonBuildingType.MAGE_GUILD;
import static ua.hudyma.domain.towns.enums.HallType.CAPITOL;

@Service
@RequiredArgsConstructor
@Log4j2
public class AbstractBuildService {
    private final TownService townService;
    private final PlayerService playerService;
    private final CommonBuildService commonBuildService;
    private final TownMapper townMapper;
    private final DwellingBuildService dwellingBuildService;
    private static final Map<Class<?>, String> TOWN_BLD_CONTAINERS = Map.of(
            CommonBuildingType.class, "commonBuildingMap",
            //CastleDwellingType.class, "dwellingMap",
            AbstractDwellingType.class, "dwellingMap",
            //test if that works as universal ancestor
            UniqueBuildingType.class, "uniqueBuildingSet",
            HordeBuildingType.class, "hordeBuildingSet",
            FortificationType.class, "fortificationType",
            HallType.class, "hallType"
    );

    @Transactional
    public String destroyBuilding(String type, String townName) {
        var town = townService.getTown(townName);
        Class<?> clazz;
        try {
            clazz = resolveBuildingEnumType(type);
        } catch (Exception e) {
            clazz = resolveDwellingEnumType(type);
        }
        var container = resolveTownBuildingContainerByType(type, clazz);
        resolveContainerEntityByStringAndExecuteDeletion(container, type, town);
        return String.format("Building %s has been demolished in %s", type, town.getName());
    }

    private void resolveContainerEntityByStringAndExecuteDeletion(
            String container, String type, Town town) {
        switch (container) {
            case "commonBuildingMap" -> town.getCommonBuildingMap().remove(CommonBuildingType.valueOf(type));
            case "dwellingMap" -> town.getDwellingMap().remove(type);
            case "uniqueBuildingSet" -> town.getUniqueBuildingSet().remove(type);
            case "hordeBuildingSet" -> town.getHordeBuildingSet().remove(type);
            case "fortificationType" -> town.setFortificationType(null);
            case "hallType" -> town.setHallType(HallType.VILLAGE_HALL);
        }
    }

    private String resolveTownBuildingContainerByType(
            String type, Class<?> clazz) {
        var container = TOWN_BLD_CONTAINERS.get(clazz);
        if (container == null) {
            throw new RequiredBuildingMissingException(
                    "Building type " + type + " COULD not be resolved");
        }
        return container;
    }

    public ResourceDemandRespDto getResourceDemand(String type, Integer level) {
        var enumClass =
                resolveBuildingEnumType(type);
        var modifiedType = getModifiedPropertiesName(type, level);
        var constantProps =
                getTypeSpecificConstantProperties(
                        modifiedType,
                        enumClass);
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
        var town = townService.getTown(dto.townName());
        checkTownBelongsToPlayer(player, town);
        var buildingType = resolve(dto.buildingType());
        if (buildingType.equals(CAPITOL))
            checkOtherTownForCapitols(player);
        var modifiedPropertiesName = getModifiedPropertiesName(
                buildingType, buildingLevel);
        var enumTypeClass = resolveBuildingEnumType(
                buildingType.toString());
        var constantProperties =
                getTypeSpecificConstantProperties(
                        modifiedPropertiesName, enumTypeClass);
        resolveBuildingTypeAndInvokeSpecificFactoryService
                (player, buildingLevel, town, buildingType, constantProperties);
        var msg = buildingLevel > 0 ? String.format
                ("%s Level %d has been erected in %s",
                        buildingType, buildingLevel, town.getName()) :
                String.format("%s has been erected in %s",
                        buildingType, town.getName());
        log.info(msg);
        return msg;
    }

    private void checkOtherTownForCapitols(Player player) {
        var townList = player.getTownsList();
        if (townList.stream().anyMatch(town -> town
                .getHallType().equals(CAPITOL))) {
            throw new BuildingAlreadyExistsException(
                    "Capitol may exist in one town ONLY");
        }
    }

    @Transactional
    public String buildDwelling(AbstractBuildReqDto dto) {
        var player = playerService.getPlayer(dto.playerId());
        var buildingLevel = dto.buildingLevel();
        if (buildingLevel > 5) {
            throw new IllegalArgumentException
                    ("Building LEVEL is limited by 5, while provided = " + buildingLevel);
        }
        var town = townService.getTown(dto.townName());
        checkTownBelongsToPlayer(player, town);
        var dwellingType = resolveDwellingType(dto.buildingType());
        var enumTypeClass = resolveDwellingEnumType
                (dwellingType.toString());
        var constantProperties =
                getTypeSpecificDwellConstantProperties(
                        dwellingType.toString(), enumTypeClass);
        resolveDwellingTypeAndInvokeSpecificFactoryService
                (player, buildingLevel, town, dwellingType, constantProperties);
        var msg = buildingLevel > 0 ? String.format(
                "%s Level %d has been erected in %s",
                dwellingType, buildingLevel, town.getName()) :
                String.format("%s has been erected in %s",
                        dwellingType, town.getName());
        log.info(msg);
        return msg;
    }

    //to be appended
    AbstractDwellingTypeProperties getTypeSpecificDwellConstantProperties(
            String propertyName, Class<? extends AbstractDwellingType> enumTypeClass) {
        if (enumTypeClass.equals(CastleDwellingType.class)) {
            return CastleDwellingTypeProperties.valueOf(propertyName);
        } else if (enumTypeClass.equals(RampartDwellingType.class)) {
            return RampartDwellingTypeProperties.valueOf(propertyName);
        } else throw new IllegalArgumentException("Unknown AbstractDwellingType propertyName: "
                + propertyName);
    }

    AbstractBuildingTypeProperties getTypeSpecificConstantProperties(
            String propertyName, Class<? extends AbstractBuildingType> enumTypeClass) {
        if (enumTypeClass.equals(CommonBuildingType.class)) {
            return CommonBuildingTypeProperties.valueOf(propertyName);
        } else if (enumTypeClass.equals(FortificationType.class)) {
            return FortificationTypeProperties.valueOf(propertyName);
        } else if (enumTypeClass.equals(HallType.class)) {
            return HallTypeProperties.valueOf(propertyName);
        } else if (enumTypeClass.equals(HordeBuildingType.class)) {
            return HordeBuildingTypeProperties.valueOf(propertyName);
        } else if (enumTypeClass.equals(UniqueBuildingType.class)) {
            return UniqueBuildingTypeProperties.valueOf(propertyName);
        } else throw new IllegalArgumentException("Unknown AbstractBuildingType propertyName: "
                + propertyName);
    }

    public Class<? extends AbstractBuildingType> resolveBuildingEnumType(String type) {
        var buildingType = resolve(type);
        for (Class<? extends AbstractBuildingType> ttype : ENUM_TYPES) {
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
        for (Class<? extends AbstractDwellingType> ttype : ENUM_DWELL_TYPES) {
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
            AbstractDwellingType dwellingType,
            AbstractDwellingTypeProperties constantProperties) {
        if (dwellingType != null /*CastleDwellingType ||
            dwellingType instanceof RampartDwellingType
            && dwellingType instanceof AbstractDwellingType*/) {
            dwellingBuildService.build(new DwellReqDto(
                    town,
                    dwellingType,
                    buildingLevel,
                    player,
                    constantProperties));
        }
        throw new IllegalArgumentException("Dwelling type is NULL, build process discarded");
    }

    private void resolveBuildingTypeAndInvokeSpecificFactoryService(
            Player player,
            int buildingLevel,
            Town town,
            AbstractBuildingType buildingType,
            AbstractBuildingTypeProperties constantProperties) {
        if (buildingType instanceof CommonBuildingType ||
                buildingType instanceof HallType ||
                buildingType instanceof FortificationType ||
                buildingType instanceof UniqueBuildingType ||
                buildingType instanceof HordeBuildingType) {
            commonBuildService.build(new BuildReqDto(
                    town,
                    buildingType,
                    buildingLevel,
                    player,
                    constantProperties));
        } else if (buildingType instanceof AbstractDwellingType) {
            throw new IllegalArgumentException("Dwelling type not acceptable");
        } else if (buildingType instanceof GrailBuildingType) {
            throw new IllegalArgumentException("GrailBuildingType not ACCEPTABLE");
        }
    }

    private static void checkTownBelongsToPlayer(
            Player player, Town town) {
        if (!player.getTownsList().contains(town)) {
            throw new IllegalStateException(town.getName() + " " +
                    "does NOT belong to " + player.getName());
        }
    }

    private String getModifiedPropertiesName(
            AbstractBuildingType buildingType,
            int buildingLevel) {
        if (buildingType == MAGE_GUILD) return buildingType + "_L" + buildingLevel;
        return buildingType.toString();
    }

    private String getModifiedPropertiesName(
            String buildingType,
            int buildingLevel) {
        if (buildingType.equals(MAGE_GUILD.name()))
            return buildingType + "_L" + buildingLevel;
        return buildingType;
    }
}
