package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.DwellReqDto;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.properties.dwelling.CastleDwellingTypeProperties;
import ua.hudyma.domain.towns.enums.properties.dwelling.RampartDwellingTypeProperties;
import ua.hudyma.enums.Faction;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.InsufficientResourcesException;
import ua.hudyma.exception.RequiredBuildingMissingException;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class DwellingBuildService {

    public void build (DwellReqDto dto) {
        var town = dto.town();
        var dwellingType = dto.dwellingType();
        var buildingLevel = dto.buildingLevel();
        var player = dto.player();
        var constantProperties = dto.constantProperties();
        var dwellingBuildingMap = town.getDwellingMap();
        if (dwellingBuildingMap == null) {
            dwellingBuildingMap = new HashMap<>();
            town.setDwellingMap(dwellingBuildingMap);
        } else {
            int existingBuildLevel;
            var resourceMap = player.getResourceMap();
            if (resourceMap == null) {
                throw new IllegalStateException("Available Resources Map is NULL");
            }
            if (dwellingType != null) {
                if (dwellingBuildingMap.containsKey(dwellingType.getCode())) {
                    existingBuildLevel = dwellingBuildingMap.get(dwellingType.getCode());
                    throwExceptionWhenLevelMatchesOrZero(town, dwellingType,
                            buildingLevel, existingBuildLevel);
                }
                checkDemands(town, resourceMap, constantProperties);
                dwellingBuildingMap.put(dwellingType.getCode(), buildingLevel);
            } else {
                throw new IllegalStateException("dwellingType is NULL");
            }
        }
    }

    /**
     * CastleDwellingType and Rampart. Add more
     */
    private void checkDemands(
            Town town, Map<ResourceType, Integer> availResources,
            AbstractDwellingTypeProperties constantProperties) {
        Map<String, Integer> demandedBuildings = Map.of();
        EnumMap<ResourceType, Integer> demandedResources = new EnumMap<>(ResourceType.class);
        if (constantProperties instanceof CastleDwellingTypeProperties) {
            demandedBuildings =
                    ((CastleDwellingTypeProperties) constantProperties).getRequiredBuildingMap();
            demandedResources = ((CastleDwellingTypeProperties) constantProperties)
                    .getRequiredResourceMap();
        } else if (constantProperties instanceof RampartDwellingTypeProperties) {
            demandedBuildings =
                    ((RampartDwellingTypeProperties) constantProperties).getRequiredBuildingMap();
            demandedResources = ((RampartDwellingTypeProperties) constantProperties)
                    .getRequiredResourceMap();
        }
        if (demandedBuildings != null &&
           !demandedBuildings.isEmpty()) {
                checkDemandedBuildings(town, demandedBuildings);
            }
        if (demandedResources != null)
            checkResourcesDemandAndDecrement(availResources, demandedResources);
    }

    private void checkResourcesDemandAndDecrement(
            Map<ResourceType, Integer> availResources,
            EnumMap<ResourceType, Integer> demandedResources) {
        var insufficientResoucesMap = new EnumMap<ResourceType, Integer>(ResourceType.class);
        for (Map.Entry<ResourceType, Integer> res : demandedResources.entrySet()) {
            var availResQty = availResources.get(res.getKey());
            var demandedResQty = res.getValue();
            var difference = availResQty - demandedResQty;
            if (difference < 0) {
                insufficientResoucesMap.put(res.getKey(), demandedResQty);
            }
            else {
                availResources.replace(res.getKey(), difference);
                log.info("{} has been decremented by {} and now = {}",
                        res.getKey(),
                        demandedResQty,
                        difference);
            }
        }
        if (!insufficientResoucesMap.isEmpty()){
            var insuffResString = "Resources DEMANDS: " + insufficientResoucesMap
                    + ", while AVAIL = " + availResources;
            throw getExceptionSupplier(ResourceType.class,
                    insuffResString,
                    InsufficientResourcesException::new,
                    true)
                    .get();
        }
    }

    private void checkDemandedBuildings(
            Town town,
            Map<String, Integer> demandedBuildings) {
        Map<String, Integer> allTownBuildingMap = getAllTownBuildingsMap(town);
        for (Map.Entry<String, Integer> entry : demandedBuildings.entrySet()) {
            var demandedBuildingType = entry.getKey();
            var demandedBuildingLevel = entry.getValue() == null ? 0 : entry.getValue();
            boolean containsKey = allTownBuildingMap.containsKey(demandedBuildingType);
//            if (demandedBuildingType.equals(FORT.name()) &&
//                    (allTownBuildingMap.containsKey(CITADEL.name()) ||
//                     allTownBuildingMap.containsKey(CASTLE.name()))){
//
//                log.info(":: FORT is demanded, while CITADEL or CASTLE has been erected");
//                //todo issues: при спробі збудувати dwarf_cottage мало місце позитивне
                //todo завершення методу зі повідомленням про успішне будівництво
            //}
            if (!containsKey) {
                var msg = demandedBuildingLevel > 0 ? String.format
                        ("Required %s of Level %d",
                        entry.getKey(),
                        demandedBuildingLevel) : String.format("Required %s",
                        entry.getKey());
                throw getExceptionSupplier(
                        msg, RequiredBuildingMissingException::new)
                        .get();
            }
        }
    }

    private static Map<String, Integer> getAllTownBuildingsMap(Town town) {
        var map = new HashMap<String, Integer>();
        var horde = town.getHordeBuildingSet();
        var unique = town.getUniqueBuildingSet();
        var dwellingMap = town.getDwellingMap();
        var commonBuldingMap = town.getCommonBuildingMap();
        var hall = town.getHallType();
        var fortification = town.getFortificationType();

        if (dwellingMap != null) {
            map.putAll(dwellingMap);
        }
        if (commonBuldingMap != null) {
            map.putAll(toStringMap(commonBuldingMap));
        }
        if (fortification != null) {
            map.put(fortification.name(), 0);
        }
        if (hall != null) {
            map.put(hall.name(), 0);
        }
        if (horde != null)
            map.putAll(toMap(horde));
        if (unique != null)
            map.putAll(toMap(unique));
        return map;
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

    private static Map<String, Integer> toMap(Set<String> set) {
        return set.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        v -> 0));
    }

    private static <T> Map<String, Integer> toStringMap(Map<T, Integer> map) {
        return map.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().toString(),
                Map.Entry::getValue
        ));
    }

}
