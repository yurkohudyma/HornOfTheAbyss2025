package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.converter.BuildingTypeResolver;
import ua.hudyma.domain.towns.dto.BuildReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.domain.towns.enums.properties.*;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.InsufficientResourcesException;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.service.*;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.MAGE_GUILD;
import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommonBuildService {
    private final PlayerService playerService;
    private final TownService townService;

    @Transactional
    public String build(BuildReqDto dto) {
        var player = playerService.getPlayer(dto.playerId());
        var buildingLevel = dto.buildingLevel() == null ? 0 : dto.buildingLevel();
        if (buildingLevel > 5) {
            throw new IllegalArgumentException
                    ("Building LEVEL is limited by 5, while provided = " + buildingLevel);
        }
        var town = townService.getTown(dto.name());
        checkTownBelongsToPlayer(player, town);
        var buildingType = BuildingTypeResolver.resolve(dto.buildingType());
        var modifiedPropertiesName = getModifiedPropertiesName(buildingType, buildingLevel);
        var enumTypeClass = resolveBuildingEnumType(buildingType.toString());
        //var convertedBuildingType = CommonBuildingType.valueOf(buildingType);
        var constantProperties =
                getTypeSpecificConstantProperties(modifiedPropertiesName, enumTypeClass);
        var commonBuildingMap = town.getCommonBuildingMap();
        if (commonBuildingMap == null) {
            commonBuildingMap = new EnumMap<>(CommonBuildingType.class);
            town.setCommonBuildingMap(commonBuildingMap);
        } else {
            var existingBuildLevel = commonBuildingMap.get((CommonBuildingType) buildingType);
            if (commonBuildingMap.containsKey((CommonBuildingType) buildingType)) {
                String message = "";
                if (existingBuildLevel == buildingLevel) {
                    message = String.format("%s of level %d already built in %s",
                            buildingType, existingBuildLevel, town.getName());
                    throw new BuildingAlreadyExistsException(message);
                } else if (existingBuildLevel == 0) {
                    message = String.format("%s already built in %s",
                            buildingType, town.getName());
                    throw new BuildingAlreadyExistsException(message);
                }
            }
        }
        checkTownDemands(town, player, (CommonBuildingTypeProperties) constantProperties);
        commonBuildingMap.put((CommonBuildingType) buildingType, buildingLevel);
        var msg = String.format("%s Level %d has been erected in %s",
                buildingType, buildingLevel, town.getName());
        log.info(msg);
        return msg;
    }

    private static AbstractBuildingTypeProperties getTypeSpecificConstantProperties(
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
            //todo dwelling types excluded from AbstractBuildingType, process separatetely
            if (typeList.contains(buildingType)) {
                return ttype;
            }
        }
        throw new IllegalArgumentException("No match for AbstractBuildingType propertyName: "
                + type);
    }

    private static <E extends Enum<E> & AbstractBuildingType>
    boolean enumContainConstant(String buildingType, Class<E> clazz) {
        try {
            Enum.valueOf(clazz, buildingType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    private String getModifiedPropertiesName(AbstractBuildingType buildingType, int buildingLevel) {
        if (buildingType == MAGE_GUILD) return buildingType + "_L" + buildingLevel;
        return buildingType.toString();
    }

    private static void checkTownBelongsToPlayer(Player player, Town town) {
        if (!player.getTownsList().contains(town)) {
            throw new IllegalStateException(town.getName() + " does NOT belong to " + player.getName());
        }
    }

    private void checkTownDemands(
            Town town, Player player,
            CommonBuildingTypeProperties constantProperties) {
        var demandedBuildings =
                constantProperties.getRequiredBuiltBuildings();
        if (!demandedBuildings.isEmpty()) {
            checkDemandedBuildings(town, demandedBuildings);
        }
        var availResources = player.getResourceMap();
        if (availResources == null) {
            throw new IllegalStateException("Available Resources Map is NULL");
        }
        var demandedResources = constantProperties
                .getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources);
    }

    private void checkDemandedBuildings(
            Town town,
            EnumMap<CommonBuildingType, Integer> demandedBuildings) {
        var commonBuildingMap = town.getCommonBuildingMap();
        // todo var dwellingBuildingList = town.getDwellingTypeList(); //include validation
        for (Map.Entry<CommonBuildingType, Integer> entry : demandedBuildings.entrySet()) {
            var demandedBuildingType = entry.getKey();
            var demandedBuildingLevel = entry.getValue() == null ? 0 : entry.getValue();
            boolean containsKey = commonBuildingMap.containsKey(demandedBuildingType);
            if (!containsKey) {
                throw getExceptionSupplier(ResourceType.class,
                        String.format("Required %s of Level %d",
                                entry.getKey(),
                                demandedBuildingLevel),
                        InsufficientResourcesException::new,
                        true)
                        .get();
            }
        }
    }

    private void checkResourcesDemandAndDecrement(
            Map<ResourceType, Integer> availResources,
            EnumMap<ResourceType, Integer> demandedResources) {
        for (Map.Entry<ResourceType, Integer> res : demandedResources.entrySet()) {
            var availResQty = availResources.get(res.getKey());
            var demandedResQty = res.getValue();
            var difference = availResQty - demandedResQty;
            if (difference < 0) {
                throw getExceptionSupplier(ResourceType.class,
                        String.format("%s: avail: %d, required: %d",
                                res.getKey(),
                                availResQty,
                                demandedResQty),
                        InsufficientResourcesException::new,
                        true)
                        .get();
            }
            availResources.replace(res.getKey(), difference);
            log.info("{} has been decremented by {} and now = {}",
                    res.getKey(),
                    demandedResQty,
                    difference);
        }
    }


}
