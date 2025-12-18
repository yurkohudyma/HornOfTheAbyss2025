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
import ua.hudyma.domain.towns.config.AbstractBuildingConfig;
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
import ua.hudyma.util.FixedSizeList;
import ua.hudyma.util.MessageProcessor;

import java.util.*;

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
        var town = getTown(dto.name());
        checkTownBelongsToPlayer(player, town);
        var buildingType = dto.buildingType();
        var constantProperties =
                CommonBuildingTypeProperties.valueOf(buildingType.name());
        checkTownDemands(town, player, constantProperties);
        var commonBuildingList = town.getCommonBuildingList();
        if (town.getCommonBuildingList() == null){
            commonBuildingList = new ArrayList<>();
            town.setCommonBuildingList(commonBuildingList);
        }
        else if (commonBuildingList.contains(buildingType.name())){
            throw new BuildingAlreadyExistsException(buildingType + " already built in " + town.getName());
        }
        commonBuildingList.add(buildingType.name());
        var msg = String.format("%s has been erected in %s", buildingType, town.getName());
        log.info(msg);
        return msg;
    }

    private static void checkTownBelongsToPlayer(Player player, Town town) {
        if (!player.getTownsList().contains(town)){
            throw new IllegalStateException(town.getName() + " does NOT belong to " + player.getName());
        }
    }

    private void checkTownDemands(
            Town town, Player player,
            CommonBuildingTypeProperties constantProperties) {
        var buildingConfig = town.getBuildingConfig();
        var availResources = player.getResourceMap();
        if (availResources == null) {
            throw new IllegalStateException("Available Resources Map is NULL");
        }
        var demandedResources = constantProperties.getRequiredResourceMap();
        checkResourcesDemandAndDecrement(availResources, demandedResources, player);
        var demandedBuildings = constantProperties.getRequiredBuiltBuildings();
        checkDemandedBuildings(buildingConfig, demandedBuildings);
    }

    private void checkDemandedBuildings(
            AbstractBuildingConfig buildingConfig,
            EnumMap<CommonBuildingType, Integer> demandedBuildings) {
        //todo implem
    }

    private void checkResourcesDemandAndDecrement(
            Map<ResourceType, Integer> availResources,
            EnumMap<ResourceType, Integer> demandedResources,
            Player player) {
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
