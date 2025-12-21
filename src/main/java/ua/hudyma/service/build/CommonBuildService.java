package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.BuildReqDto;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.domain.towns.enums.properties.*;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.InsufficientResourcesException;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommonBuildService {

    public void build(BuildReqDto dto) {
        var town = dto.town();
        var buildingType = dto.buildingType();
        var buildingLevel = dto.buildingLevel();
        var player = dto.player();
        var constantProperties = dto.constantProperties();
        var commonBuildingMap = town.getCommonBuildingMap();
        if (commonBuildingMap == null) {
            commonBuildingMap = new EnumMap<>(CommonBuildingType.class);
            town.setCommonBuildingMap(commonBuildingMap);
        } else {
            int existingBuildLevel;
            if (buildingType instanceof CommonBuildingType) {
                if (commonBuildingMap.containsKey(buildingType)) {
                    existingBuildLevel = commonBuildingMap.get(buildingType);
                    throwExceptionWhenLevelMatchesOrZero(town, buildingType,
                            buildingLevel, existingBuildLevel);
                }
                checkDemands(town, player,
                        (CommonBuildingTypeProperties) constantProperties);
                commonBuildingMap.put((CommonBuildingType) buildingType, buildingLevel);
            } else if (buildingType instanceof HallType) {
                if (town.getHallType().equals(buildingType)){
                    var message = String.format("%s already built in %s",
                            buildingType, town.getName());
                    throw new BuildingAlreadyExistsException(message);
                }
                checkDemands(town, player, (HallTypeProperties) constantProperties);
                town.setHallType((HallType) buildingType);
            } else {
                throw new IllegalStateException("buildingType is of WRONG instance type = "
                        + buildingType.getClass().getName());
            }
        }
    }

    private static void throwExceptionWhenLevelMatchesOrZero(
            Town town, AbstractBuildingType buildingType,
            int buildingLevel, int existingBuildLevel) {
        String message;
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

    private void checkDemands(
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
    /** HallType and other one-field Enum properties method */
     private void checkDemands (Town town, Player player, HallTypeProperties constantProperties){
        var demandedBuildings = constantProperties.getRequiredBuildingMap();
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

    /**
     * HallType and other one-field Enum properties method
     * @param town
     * @param demandedBuildings
     */
    private void checkDemandedBuildings (Town town, Map<String, Integer> demandedBuildings){
        var commonBuildingMap = town.getCommonBuildingMap();
        var stringifiedBuildingMap = convertToStringKeyMap(commonBuildingMap);
        for (Map.Entry<String, Integer> entry : demandedBuildings.entrySet()) {
            var demandedBuildingType = entry.getKey();
            var demandedBuildingLevel = entry.getValue() == null ? 0 : entry.getValue();
            boolean containsKey = stringifiedBuildingMap.containsKey(demandedBuildingType);
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

    private Map<String, Integer> convertToStringKeyMap(Map<CommonBuildingType, Integer> commonBuildingMap) {
        return commonBuildingMap.entrySet().stream()
                .collect(Collectors
                        .toMap(k -> k.getKey().toString(), Map.Entry::getValue));
    }

    private void checkResourcesDemandAndDecrement(
            Map<ResourceType, Integer> availResources,
            EnumMap<ResourceType, Integer> demandedResources) {
        for (Map.Entry<ResourceType, Integer> res : demandedResources.entrySet()) {
            var availResQty = availResources.get(res.getKey());
            var demandedResQty = res.getValue();
            var difference = availResQty - demandedResQty;
            if (difference < 0) { //todo do not throw exc immediately, collect all not conforming
                // todo units and finalise after all checks have been provided
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