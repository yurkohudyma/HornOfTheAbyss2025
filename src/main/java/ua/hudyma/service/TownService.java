package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.BuildReqDto;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.properties.CommonBuildingTypeProperties;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.InsufficientResourcesException;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.repository.TownRepository;
import ua.hudyma.resource.ResourceDemandRespDto;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.util.MessageProcessor;

import java.util.*;

import static ua.hudyma.domain.towns.enums.CommonBuildingType.MAGE_GUILD;
import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class TownService {
    private final TownRepository townRepository;
    private final TownMapper townMapper;
    private final HeroService heroService;
    private final CombatService combatService;
    private final ArmyService armyService;
    private final PlayerService playerService;

    @Transactional
    public String build(BuildReqDto dto) {
        var player = playerService.getPlayer(dto.playerId());
        var buildingLevel = dto.buildingLevel() == null ? 0 : dto.buildingLevel();
        if (buildingLevel > 5){
            throw new IllegalArgumentException
                    ("Building LEVEL is limited by 5, while provided = " + buildingLevel);
        }
        var town = getTown(dto.name());
        checkTownBelongsToPlayer(player, town);
        var buildingType = dto.buildingType();
        String modifiedPropertiesName = getModifiedPropertiesName(buildingType, buildingLevel);
        var constantProperties =
                CommonBuildingTypeProperties.valueOf(modifiedPropertiesName);
        var commonBuildingMap = town.getCommonBuildingMap();
        if (commonBuildingMap == null){
            commonBuildingMap = new EnumMap<>(CommonBuildingType.class);
            town.setCommonBuildingMap(commonBuildingMap);
        }
        else {
            var existingBuildLevel = commonBuildingMap.get(buildingType);
            if (commonBuildingMap.containsKey(buildingType)){
                String message = "";
                    if (existingBuildLevel == buildingLevel) {
                        message = String.format("%s of level %d already built in %s",
                                buildingType, existingBuildLevel, town.getName());
                        throw new BuildingAlreadyExistsException(message);
                    }
                    else if (existingBuildLevel == 0) {
                        message = String.format("%s already built in %s",
                                buildingType, town.getName());
                        throw new BuildingAlreadyExistsException(message);
                    }
            }
        }
        checkTownDemands(town, player, constantProperties);
        commonBuildingMap.put(buildingType, buildingLevel);
        var msg = String.format("%s Level %d has been erected in %s",
                buildingType, buildingLevel, town.getName());
        log.info(msg);
        return msg;
    }

    private String getModifiedPropertiesName(CommonBuildingType buildingType, int buildingLevel) {
        if (buildingType == MAGE_GUILD) return buildingType.name() + "_L" + buildingLevel;
        return buildingType.name();
    }

    private static void checkTownBelongsToPlayer(Player player, Town town) {
        if (!player.getTownsList().contains(town)){
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
        //todo ideally you should receive single buildConfig where all info is stored
        //todo otherwise every other kind of building is fetched and compared independently
        var dwellingBuildingList = town.getDwellingTypeList(); //include validation
        for (Map.Entry<CommonBuildingType, Integer> entry : demandedBuildings.entrySet()){
            var demandedBuildingType = entry.getKey();
            var demandedBuildingLevel = entry.getValue() == null ? 0 : entry.getValue();
            boolean containsKey = commonBuildingMap.containsKey(demandedBuildingType);
            //var existingBuildingLevel = commonBuildingMap.get(demandedBuildingType) == null ? 0
              //      : commonBuildingMap.get(demandedBuildingType);
            if (!containsKey){
                throw getExceptionSupplier(ResourceType.class,
                        String.format("Required %s of Level %d",
                                entry.getKey(),
                                demandedBuildingLevel),
                        InsufficientResourcesException::new,
                        true)
                        .get();
            }
            /*else if (existingBuildingLevel >= demandedBuildingLevel ){
                throw new BuildingAlreadyExistsException(demandedBuildingType +
                        " of Level " + existingBuildingLevel + " already built in " + town.getName());
            }*/
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

    public ResourceDemandRespDto getResourceDemand(String type) {
        var constantProps =
                CommonBuildingTypeProperties.valueOf(type);
        return townMapper.mapToResourceDto(constantProps);
    }

    @SneakyThrows
    public String createTown(TownReqDto dto) {
        var town = townMapper.toEntity(dto);
        townRepository.save(town);
        return MessageProcessor
                .getReturnMessage(town, "name");
    }

    public TownRespDto fetchTown(String name) {
        var town = getTown(name);
        return townMapper.toDto(town);
    }

    @Transactional
    public String allocateVisitingHero(String heroId, String townName) {
        var town = getTown(townName);
        var incomingHero = heroService.getHero(heroId);
        var visitingHero = town.getVisitingHero();
        if (town.getPlayer() != incomingHero.getPlayer()) {
            combatService.initTownBattle(incomingHero, town);
            log.error("Town is OCCUPIED by enemy player {}",
                    town.getPlayer().getName());
            //todo if battle succeeds, proceed with allocation
        } else if (incomingHero == visitingHero || incomingHero == town.getGarrisonHero()) {
            return String.format("Hero %s is ALREADY in %s",
                    incomingHero.getName(), town.getName());
        } else if (visitingHero != null) {
            swapHeroesAtTownGarrison(incomingHero, visitingHero, town);
            if (town.getGarrisonArmy() != null) { //todo in reality visiting incomingHero
                // todo does not upscale creature until garrison army has been transferred to him
                upgradeGarnisonSkillsByHero(town, incomingHero);
            }
            return String.format("Hero %s is now garnisoned in %s, while %s is Visitor",
                    incomingHero.getName(), town.getName(), visitingHero.getName());
        } else {
            town.setVisitingHero(incomingHero);
        }
        return String.format("Hero %s is now visiting %s", incomingHero.getName(), town.getName());
    }

    private void upgradeGarnisonSkillsByHero(Town town, Hero hero) {
        var upgradedGarrisonArmy = armyService
                .upgradeArmySkillToHero(town.getGarrisonArmy(), hero);
        town.setGarrisonArmy(upgradedGarrisonArmy);
    }


    private void swapHeroesAtTownGarrison(Hero incomingHero, Hero visitingHero, Town town) {
        town.setGarrisonHero(visitingHero);
        town.setVisitingHero(incomingHero);
        log.info("Hero {} is now garnisoned in {}, while {} is Visitor",
                town.getGarrisonHero().getName(),
                town.getName(),
                town.getVisitingHero().getName());
    }

    @Transactional
    public String swapHeroesInTown(String townName) {
        var town = getTown(townName);
        var visitor = town.getVisitingHero();
        var garnisoner = town.getGarrisonHero();
        if (visitor == null || garnisoner == null) {
            return "Either visiting or garnison hero is missing";
        }
        swapHeroesAtTownGarrison(garnisoner, visitor, town);
        upgradeGarnisonSkillsByHero(town, visitor);
        return String.format("Heroes [%s <-- --> %s] rotated in town",
                garnisoner.getName(),
                visitor.getName());
    }

    private Town getTown(String name) {
        return townRepository.findByName(name)
                .orElseThrow(getExceptionSupplier(Town.class,
                        name,
                        EntityNotFoundException::new,
                        false));
    }
}
