package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.DwellReqDto;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.domain.towns.enums.dwelling.CastleDwellingType;
import ua.hudyma.domain.towns.enums.properties.CastleDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.properties.CommonBuildingTypeProperties;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.InsufficientResourcesException;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class DwellingBuildService {

    public void build(DwellReqDto dto) {
        var town = dto.town();
        var buildingType = dto.buildingType();
        var buildingLevel = dto.buildingLevel();
        var player = dto.player();
        var constantProperties = dto.constantProperties();
        var dwellingBuildingMap = town.getDwellingMap();
        if (dwellingBuildingMap == null) {
            dwellingBuildingMap = new HashMap<>();
            town.setDwellingMap(dwellingBuildingMap);
        } else {
            int existingBuildLevel;
            var alreadyBuiltMsg = String.format("%s already built in %s",
                    buildingType, town.getName());
            var resourceMap = player.getResourceMap();
            if (resourceMap == null) {
                throw new IllegalStateException("Available Resources Map is NULL");
            }
            if (buildingType instanceof CastleDwellingType) {
                if (dwellingBuildingMap.containsKey(buildingType.getCode())) {
                    existingBuildLevel = dwellingBuildingMap.get(buildingType.getCode());
                    throwExceptionWhenLevelMatchesOrZero(town, buildingType,
                            buildingLevel, existingBuildLevel);
                }
                checkDemands(town, resourceMap,
                        (CastleDwellingTypeProperties)                                constantProperties);
                dwellingBuildingMap.put(buildingType.getCode(), buildingLevel);
            } else {
                throw new IllegalStateException("buildingType is of WRONG instance type = "
                        + buildingType.getClass().getName());
            }
        }
    }

    /** CastleDwellingType */
    private void checkDemands(
            Town town,  Map<ResourceType, Integer> availResources,
            CastleDwellingTypeProperties constantProperties) {
        var demandedBuildings =
                constantProperties.getRequiredBuildingMap();
        if (!demandedBuildings.isEmpty()) {
            checkDemandedBuildings(town, demandedBuildings);
        }
        var demandedResources = constantProperties
                .getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources);
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

    private void checkDemandedBuildings(
            Town town,
            Map<String, Integer> demandedBuildings) {
        var dwellingMap = town.getDwellingMap();
        for (Map.Entry<String, Integer> entry : demandedBuildings.entrySet()) {
            var demandedBuildingType = entry.getKey();
            var demandedBuildingLevel = entry.getValue() == null ? 0 : entry.getValue();
            boolean containsKey = dwellingMap.containsKey(demandedBuildingType);
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

    private void throwExceptionWhenLevelMatchesOrZero(
            Town town, AbstractDwellingType buildingType,
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
}
