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
import ua.hudyma.exception.RequiredBuildingMissingException;
import ua.hudyma.resource.enums.ResourceType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.MARKETPLACE;
import static ua.hudyma.domain.towns.enums.CommonBuildingType.RESOURCE_SILO;
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
            var alreadyBuiltMsg = String.format("%s already built in %s",
                    buildingType, town.getName());
            var resourceMap = player.getResourceMap();
            if (resourceMap == null) {
                throw new IllegalStateException("Available Resources Map is NULL");
            }
            if (buildingType instanceof CommonBuildingType) {
                if (commonBuildingMap.containsKey(buildingType)) {
                    existingBuildLevel = commonBuildingMap.get(buildingType);
                    throwExceptionWhenLevelMatchesOrZero(town, buildingType,
                            buildingLevel, existingBuildLevel);
                }
                checkDemands(town, resourceMap,
                        (CommonBuildingTypeProperties)
                                constantProperties);
                if (buildingType == RESOURCE_SILO){
                    commonBuildingMap.remove(MARKETPLACE);
                    commonBuildingMap.put((CommonBuildingType) buildingType, 0);
                }
                else {
                    commonBuildingMap.put((CommonBuildingType) buildingType, buildingLevel);
                }

            } else if (buildingType instanceof HallType) {
                if (town.getHallType().equals(buildingType)){
                    throw getExceptionSupplier(alreadyBuiltMsg,
                            BuildingAlreadyExistsException::new)
                            .get();
                }
                checkDemands(town, resourceMap, (HallTypeProperties)
                        constantProperties);
                town.setHallType((HallType) buildingType);

            } else if (buildingType instanceof FortificationType) {
                var fortificationType = town.getFortificationType();
                if (fortificationType != null && fortificationType.equals(buildingType)){
                    throw getExceptionSupplier(alreadyBuiltMsg,
                            BuildingAlreadyExistsException::new)
                            .get();
                }
                checkDemands(town, resourceMap,
                        (FortificationTypeProperties)
                        constantProperties);
                town.setFortificationType((FortificationType) buildingType);

            } else if (buildingType instanceof UniqueBuildingType) {
                var uniqueBuildings = town.getUniqueBuildingSet();
                if (uniqueBuildings != null &&
                        uniqueBuildings.contains(((UniqueBuildingType) buildingType).name())){
                    throw getExceptionSupplier(alreadyBuiltMsg,
                            BuildingAlreadyExistsException::new)
                            .get();
                }
                checkDemands(town, resourceMap,
                        (UniqueBuildingTypeProperties)
                        constantProperties);
                if (uniqueBuildings == null) uniqueBuildings = new HashSet<>();
                town.setUniqueBuildingSet(uniqueBuildings);
                uniqueBuildings.add(((UniqueBuildingType) buildingType).name());

            } else if (buildingType instanceof HordeBuildingType) {
                var hordeBuildings = town.getUniqueBuildingSet();
                if (hordeBuildings != null &&
                        hordeBuildings.contains(((HordeBuildingType) buildingType).name())){
                    throw getExceptionSupplier(alreadyBuiltMsg,
                            BuildingAlreadyExistsException::new)
                            .get();
                }
                checkDemands(town, resourceMap,
                        (HordeBuildingTypeProperties)
                                constantProperties);
                if (hordeBuildings == null) hordeBuildings = new HashSet<>();
                town.setUniqueBuildingSet(hordeBuildings);
                hordeBuildings.add(((HordeBuildingType) buildingType).name());

            } else {
                throw new IllegalStateException("buildingType is of WRONG instance type = "
                        + buildingType.getClass().getName());
            }
        }
    }

    /** CommonBuildingType */
    private void checkDemands(
            Town town,  Map<ResourceType, Integer> availResources,
            CommonBuildingTypeProperties constantProperties) {
        var demandedBuildings =
                constantProperties.getRequiredBuiltBuildings();
        if (!demandedBuildings.isEmpty()) {
            checkDemandedBuildings(town, demandedBuildings);
        }

        var demandedResources = constantProperties
                .getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources);
    }

    /** HallType */
     private void checkDemands (Town town, Map<ResourceType, Integer> availResources,
                                HallTypeProperties constantProperties){
        var demandedBuildings = constantProperties.getRequiredBuildingMap();
        if (!demandedBuildings.isEmpty()) {
            checkDemandedBuildings(town, demandedBuildings);
        }
        var demandedResources = constantProperties
                .getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources);
    }
    /** FortificationType */
    private void checkDemands (Town town, Map<ResourceType, Integer> availResources,
                               FortificationTypeProperties constantProperties){
        var demandedBuildings = constantProperties.getRequiredBuildingSet();
        if (!demandedBuildings.isEmpty()) {
            checkDemandedBuildings(town, demandedBuildings);
        }
        var demandedResources = constantProperties
                .getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources);
    }
    /** UniqueType */
    private void checkDemands (Town town, Map<ResourceType, Integer> availResources,
                               UniqueBuildingTypeProperties constantProperties){
        var demandedBuildings = constantProperties.getRequiredBuildingSet();
        if (!demandedBuildings.isEmpty()) {
            checkDemandedBuildings(town, demandedBuildings);
        }
        var demandedResources = constantProperties
                .getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources);
    }
    /** HordeType */
    private void checkDemands (Town town, Map<ResourceType, Integer> availResources,
                               HordeBuildingTypeProperties constantProperties){
        var demandedBuildings = constantProperties.getRequiredBuildingSet();
        if (!demandedBuildings.isEmpty()) {
            checkHordeDemandedBuildings(town, demandedBuildings);
        }
        var demandedResources = constantProperties
                .getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources);
    }

    /** CommonBuildingType */
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
    /** HordeType */
    private void checkHordeDemandedBuildings (Town town,
                                              Set<String> demandedBuildings){
        var commonBuildingMap = town
                .getCommonBuildingMap();
        var stringifiedExistingBuildingMap =
                convertToStringKeyMap(commonBuildingMap);
        if (town.getHordeBuilding() != null){
            stringifiedExistingBuildingMap.putAll(toMap(
                    town.getHordeBuilding()));
        }
        for (String demanded: demandedBuildings){
            if (!stringifiedExistingBuildingMap.containsKey(demanded)){
                throw getExceptionSupplier(
                        String.format("Required %s", demanded),
                        RequiredBuildingMissingException::new)
                        .get();
            }
        }

    }

    /** UniqueType */
    private void checkDemandedBuildings(Town town,
                                        Set<String> demandedBuildings){
        var commonBuildingMap = town
                .getCommonBuildingMap();
        var stringifiedExistingBuildingMap =
                convertToStringKeyMap(commonBuildingMap);
        if (town.getUniqueBuildingSet() != null){
            stringifiedExistingBuildingMap.putAll(toMap(
                    town.getUniqueBuildingSet()));
        }
        for (String demanded: demandedBuildings){
            if (!stringifiedExistingBuildingMap.containsKey(demanded)){
                throw getExceptionSupplier(
                        String.format("Required %s", demanded),
                        RequiredBuildingMissingException::new)
                        .get();
            }
        }
    }

        /** FortificationType */
    private void checkDemandedBuildings(Town town,
                                        EnumSet<FortificationType> demandedBuildings) {
        var commonBuildingMap = town
                .getCommonBuildingMap();
        var stringifiedExistingBuildingMap =
                convertToStringKeyMap(commonBuildingMap);
        if (town.getFortificationType() != null){
            stringifiedExistingBuildingMap.put(town.getFortificationType().toString(), 0);
        }
        for (FortificationType demanded : demandedBuildings){
            if (!stringifiedExistingBuildingMap.containsKey(demanded.name())){
                throw getExceptionSupplier(
                        String.format("Required %s", demanded.name()),
                        RequiredBuildingMissingException::new)
                        .get();
            }
        }
    }

    /** HallType */
    private void checkDemandedBuildings (Town town,
                                         Map<String, Integer> demandedBuildings){
        var commonBuildingMap = town.getCommonBuildingMap();
        var stringifiedExistingBuildingMap =
                convertToStringKeyMap(commonBuildingMap);
        if (town.getHallType() != null){
            stringifiedExistingBuildingMap.put(town.getHallType().toString(), 0);
        }
        if (town.getFortificationType() != null){
            stringifiedExistingBuildingMap.put(town.getFortificationType().name(), 0);
        }
        for (Map.Entry<String, Integer> entry : demandedBuildings.entrySet()) {
            var demandedBuildingType = entry.getKey();
            var demandedBuildingLevel = entry.getValue() == null ? 0 : entry.getValue();
            var demandedBuildingExistsInTown = stringifiedExistingBuildingMap
                    .containsKey(demandedBuildingType);
            var simpleBuildRequireMsg = String.format("Required %s",
                    entry.getKey());
            if (!demandedBuildingExistsInTown) {
               throw getExceptionSupplier(simpleBuildRequireMsg,
                        RequiredBuildingMissingException::new)
                        .get();
            }
            var existingBuildingTypeLevel = stringifiedExistingBuildingMap
                    .get(demandedBuildingType);
            boolean buildingLevelMatchesOrHigher = existingBuildingTypeLevel
                    >= demandedBuildingLevel;
            if (!buildingLevelMatchesOrHigher) {
                var msg = existingBuildingTypeLevel > 0 ?
                        String.format("Required %s of Level %d",
                                entry.getKey(),
                                demandedBuildingLevel) : simpleBuildRequireMsg;
                throw getExceptionSupplier(
                        msg,
                        RequiredBuildingMissingException::new)
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

    private Map<String, Integer> convertToStringKeyMap(
            Map<CommonBuildingType, Integer> commonBuildingMap) {
        return commonBuildingMap.entrySet().stream()
                .collect(Collectors
                        .toMap(k -> k.getKey().toString(),
                                Map.Entry::getValue));
    }

    private static Map<String, Integer> toMap(Set<String> demandedBuildings) {
        return demandedBuildings.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        v -> 0));
    }
}