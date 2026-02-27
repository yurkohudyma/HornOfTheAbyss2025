package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.creatures.Creature;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.converter.CreatureTypeRegistry;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.dto.ModifiableData;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.converter.AbstractDwellingTypeRegistry;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.HordeBuildingType;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.dto.TownGenerCreaturesReport;
import ua.hudyma.dto.TownHireCreaturesReqDto;
import ua.hudyma.enums.Faction;
import ua.hudyma.exception.ArmyFreeSlotOverflowException;
import ua.hudyma.exception.InsufficientResourcesException;
import ua.hudyma.exception.NoAvailableCreaturesForHireException;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.repository.TownRepository;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.util.MessageProcessor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static ua.hudyma.service.ArmyService.ARMY_SLOT_MAX_QTY;
import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class TownService {
    private final CreatureService creatureService;
    private final TownRepository townRepository;
    private final TownMapper townMapper;
    private final HeroService heroService;
    private final CombatService combatService;
    private final PlayerService playerService;
    private final ArmyHeroService armyHeroService;
    private final ArmyService armyService;

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
            if (town.getGarrisonArmy() != null) { //in reality visiting incomingHero
                // does not upscale creature until garrison army has been transferred to him
                upgradeGarnisonSkillsByHero(town, incomingHero);
            }
            return String.format("Hero %s is now garnisoned in %s, while %s is Visitor",
                    incomingHero.getName(), town.getName(), visitingHero.getName());
        } else {
            town.setVisitingHero(incomingHero);
        }
        return String.format("Hero %s is now visiting %s", incomingHero.getName(), town.getName());
    }

    @Transactional
    public List<TownGenerCreaturesReport> generateWeeklyCreatures(Long playerId) {
        var player = playerService.getPlayer(playerId);
        var townList = player.getTownsList();
        var list = new ArrayList<TownGenerCreaturesReport>();
        for (Town town : townList) {
            var reportDto = retrieveTownDwellingsAndGenerateCreatures(town);
            list.add(reportDto);
        }
        return list;
    }

    public TownGenerCreaturesReport getAvailCreaturesForHire(String townName) {
        var town = getTown(townName);
        var dwellingMap = town.getDwellingMap();
        var reportMap = new HashMap<CreatureType, Integer>();
        for (Map.Entry<String, Integer> entry : dwellingMap.entrySet()) {
            var dwellingName = entry.getKey();
            var creature = getCreatureFromDwelling(dwellingName);
            if (creature == null) continue;
            reportMap.put(creature.getCreatureType(), entry.getValue());
        }
        reportMap = getValueSortedMap(reportMap);
        return new TownGenerCreaturesReport(townName, reportMap);
    }

    @Transactional
    public List<CreatureSlot> hireCreatures(TownHireCreaturesReqDto dto) {
        var reqMap = dto.reqMap();
        if (reqMap == null || reqMap.isEmpty()) {
            throw new IllegalArgumentException("Req map cannot be null or empty");
        }
        var townName = dto.townName();
        var town = getTown(townName);
        if (!checkAvailableCreaturesForHire(town)) {
            throw new NoAvailableCreaturesForHireException("No avail creatures in " + townName);
        }
        var player = town.getPlayer();
        if (!player.getTownsList().contains(town)) throw new IllegalStateException
                (townName + " does NOT belong to " + player.getName());
        var hero = heroService.getHero(dto.heroId());
        var heroArmy = hero.getArmyList();
        if (heroArmy.size() == ARMY_SLOT_MAX_QTY) {
            throw new ArmyFreeSlotOverflowException("No free slots for hiring creatures");
        }
        var resourcesMap = player.getResourceMap();
        var availCreaturesMap = getAvailCreaturesForHire(townName)
                .generCreatureMap();
        var newSlotsList = new ArrayList<CreatureSlot>();
        var dwellingMap = town.getDwellingMap();
        for (Map.Entry<CreatureType, Integer> entry : reqMap.entrySet()){
            var creatureType = entry.getKey();
            var reqQty = entry.getValue();
            if (availCreaturesMap.containsKey(creatureType)){
                var availCreatureQty = availCreaturesMap.get(creatureType);
                var creatureResourcePriceMap = getCreatureResourceMapFromCreatureType(
                        (creatureType.toString()));
                if (creatureResourcePriceMap == null || creatureResourcePriceMap.isEmpty())
                    throw new IllegalArgumentException("Creature Resource Map is null or empty, reinstate one before hire");
                if (reqQty > availCreatureQty){
                    log.error("{} is only {} left, while you ask {}", creatureType, availCreatureQty, reqQty);
            }
                else {
                    checkResourceAvailableForCreatureHire(resourcesMap, player.getResourceMap());
                    //todo implement resources decrementing upon successfull hiring
                    var newSlot = new CreatureSlot();
                    newSlot.setType(creatureType);
                    newSlot.setQuantity(reqQty);
                    newSlotsList.add(newSlot);
                    //todo retrieve dwelling NAME and insert
                    var dwellingName = retrieveTownDwelling(creatureType, town);
                    dwellingMap.put(String.valueOf(dwellingName), availCreatureQty - reqQty);
                    town.setDwellingMap(dwellingMap);
                }
            }
        }
        heroArmy.addAll(newSlotsList);
        armyHeroService.syncArmySkillsWithHero(newSlotsList, hero);
        return newSlotsList;
    }

    private AbstractDwellingType retrieveTownDwelling(CreatureType creatureType, Town town) {
        throw new IllegalCallerException("Method not implemented");
    }

    private static void checkResourceAvailableForCreatureHire(
            Map<ResourceType, Integer> reqResourcesMap,
            Map<ResourceType, Integer> availResourceMap) {
        for (Map.Entry<ResourceType, Integer> reqEntry : reqResourcesMap.entrySet()){
            var resourceName = reqEntry.getKey();
            var reqResQty = reqEntry.getValue();
            var availResQty = availResourceMap.get(resourceName);
            if (availResQty < reqResQty) throw new InsufficientResourcesException("No enough "
                    + resourceName + ": available : " + availResQty + ", while required " + reqResQty);
        }
    }

    private static Map<ResourceType, Integer> getCreatureResourceMapFromCreatureType
            (String creatureType) {
        return CreatureTypeRegistry
                .fromCode(creatureType)
                .getRequiredResourceMap();
    }

    private boolean checkAvailableCreaturesForHire(Town town) {
        return town.getDwellingMap().values().stream().anyMatch(a -> a > 0);
    }

    private Creature getCreatureFromDwelling(String dwellingName) {
        var specificDwellingEnum = AbstractDwellingTypeRegistry
                .fromCode(dwellingName);
        var creatureEnum = specificDwellingEnum.getCreature();        
        return creatureService
                .fetchCreatureByType(creatureEnum);
    }

    private TownGenerCreaturesReport retrieveTownDwellingsAndGenerateCreatures
            (Town town) {
        var townDwellingMap = town.getDwellingMap();
        if (townDwellingMap == null) throw new IllegalArgumentException
                ("Dwelling map for town "
                        + town.getName() + " has not been created YET");
        var townHordeBuildingList =
                retrieveHordeBuildingTypeByFaction(town.getFaction());
        var reportMap = new HashMap<CreatureType, Integer>();
        for (Map.Entry<String, Integer> entry : townDwellingMap.entrySet()) {
            var dwellingName = entry.getKey();
            var creature = getCreatureFromDwelling(dwellingName);
            if (creature == null) continue;
            var creatureGrowth = retrieveCreatureGrowth(creature);
            var hordeCreatureBoost = 0;
            if (!townHordeBuildingList.isEmpty()) {
                hordeCreatureBoost = getHordeBuildingCreatureBoost
                        (creature, townHordeBuildingList);
            }
            int modifiedValue = creatureGrowth + hordeCreatureBoost;
            entry.setValue(entry.getValue() + modifiedValue);
            reportMap.put(creature.getCreatureType(), modifiedValue);
            townDwellingMap.put(entry.getKey(), entry.getValue());
            reportMap = getValueSortedMap(reportMap);
        }
        return new TownGenerCreaturesReport
                (town.getName(), reportMap);
    }

    @Nonnull
    private LinkedHashMap<CreatureType, Integer> getValueSortedMap(
            HashMap<CreatureType, Integer> reportMap) {
        return reportMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(
                        Map.Entry::getValue,
                        Comparator.reverseOrder()
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

    }

    private Integer getHordeBuildingCreatureBoost(
            Creature creature, List<HordeBuildingType> townHordeBuildingList) {
        for (HordeBuildingType horde : townHordeBuildingList) {
            if (horde.getCreatureType() == creature.getCreatureType()) {
                return horde.getCreatureBoost();
            }
        }
        return 0;
    }

    private static Integer retrieveCreatureGrowth(Creature creature) {
        var creatureSkillMap = creature.getCreatureSkillMap();
        var growth = creatureSkillMap.get(CreatureSkill.GROWTH);
        return growth.multipliedValue() == null ? growth.value() : growth.multipliedValue();
    }

    private static List<HordeBuildingType> retrieveHordeBuildingTypeByFaction(Faction townFaction) {
        return Arrays
                .stream(HordeBuildingType.values())
                .filter(faction -> faction
                        .getFaction() == townFaction)
                .toList();
    }

    private void upgradeGarnisonSkillsByHero(Town town, Hero hero) {
        var upgradedGarrisonArmy = armyHeroService
                .syncArmySkillsWithHero(town.getGarrisonArmy(), hero);
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

    public Town getTown(String name) {
        return townRepository.findByName(name)
                .orElseThrow(getExceptionSupplier(Town.class,
                        name,
                        EntityNotFoundException::new,
                        false));
    }
}
