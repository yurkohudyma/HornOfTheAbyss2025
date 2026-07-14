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
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.enums.SecondarySkill;
import ua.hudyma.domain.heroes.enums.SkillLevel;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.resource.enums.ResourceType;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.domain.spells.enums.SpellReplaceDemands;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.converter.AbstractDwellingTypeRegistry;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.domain.towns.enums.FortificationType;
import ua.hudyma.domain.towns.enums.GrailBuildingType;
import ua.hudyma.domain.towns.enums.HordeBuildingType;
import ua.hudyma.dto.TownGenerCreaturesReport;
import ua.hudyma.dto.TownHireCreaturesReqDto;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;
import ua.hudyma.exception.*;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.repository.HeroRepository;
import ua.hudyma.repository.PlayerRepository;
import ua.hudyma.repository.TownRepository;
import ua.hudyma.util.IdGenerator;
import ua.hudyma.util.MessageProcessor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static ua.hudyma.domain.creatures.enums.creaturetypes.InfernoCreatureType.FAMILIAR;
import static ua.hudyma.domain.creatures.enums.creaturetypes.InfernoEssentialCreatureType.IMP;
import static ua.hudyma.domain.spells.converter.SpellRegistry.generateRandomSpell;
import static ua.hudyma.service.ArmyService.ARMY_SLOT_MAX_QTY;
import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class TownService {

    private final CreatureService creatureService;

    private final TownRepository townRepository;

    private final TownMapper townMapper;

    //private final HeroService heroService; //generates circular
    private final CombatService combatService;

    //private final PlayerService playerService; //generates circular
    private final ArmyHeroService armyHeroService;

    private final HeroRepository heroRepository;

    private final PlayerRepository playerRepository;
    private static boolean isUnoccupied(Town town) {
        return town.getVisitingHero() == null;
    }

    //private DwellingBuildService dwellingBuildService;

    //private final SpellService spellService; generates circular

    public Map<ResourceType, Integer> calcAllHireableCreatures(String townName) {
        var town = getTown(townName);
        var dwellingMap = town.getDwellingMap();
        if (dwellingMap == null) throw new IllegalStateException("Dwelling map is null");
        var reportMap = new EnumMap<ResourceType, Integer>(ResourceType.class);
        for (Map.Entry<String, Integer> entry : dwellingMap.entrySet()) {
            var dwelling = entry.getKey();
            var creaturesQty = entry.getValue();
            var creatureReqResourceMap =
                    AbstractDwellingTypeRegistry
                            .fromCode(dwelling)
                            .getCreature()
                            .getRequiredResourceMap();
            for (Map.Entry<ResourceType, Integer> resourceMapEntry :
                    creatureReqResourceMap.entrySet()) {
                reportMap.merge(
                        resourceMapEntry.getKey(),
                        resourceMapEntry.getValue() * creaturesQty,
                        Integer::sum);
            }
        }
        return reportMap;
    }

    //todo test two-level dwelling building

    @Transactional
    public String replaceTownSpell(String townName, String existingSpellName) {
        var town = getTown(townName);
        var spellMap = town.getMagicGuildSpellMap();
        var spellEnum = SpellRegistry.fromCode(existingSpellName);
        var spellLevel = spellEnum.getSpellLevel();
        var specificLevelSpellSet = spellMap.get(spellLevel);
        if (!specificLevelSpellSet.contains(existingSpellName)) {
            throw new SpellReplaceException("Spell " + existingSpellName + " is not in " + townName
                    + " magic spell set");
        }
        var randomlyGeneratedSpell = generateRandomSpell(spellLevel);
        retrieveResourcesDemandsAndProceedConsuming(town.getPlayer(), spellLevel);
        specificLevelSpellSet.remove(existingSpellName);
        var spellName = randomlyGeneratedSpell.getName();
        specificLevelSpellSet.add(spellName);
        spellMap.put(spellLevel, specificLevelSpellSet);
        return existingSpellName + " was replaced by " + spellName + " in " + townName;
    }

    private static void retrieveResourcesDemandsAndProceedConsuming(
            Player player, Integer spellLevel) {
        var resourceDemands = SpellReplaceDemands.values();
        var demandMap = Arrays
                .stream(resourceDemands)
                .filter(spell -> spell.ordinal() + 1 == spellLevel)
                .findAny()
                .orElseThrow(() -> new SpellCastException
                        ("Requested spellLevel conversion error")).getResourceMap();
        var playerResourceMap = player.getResourceMap();
        checkResourcesDemandsAndConsumeIfMet(demandMap, playerResourceMap);
    }

    /**
     * Declarative stylewise method
     */
    @Transactional(readOnly = true)
    public Map<FortificationType, Long> getTownFortificationStats(Long playerId) {
        var player = getPlayer(playerId);
        return player.getTownsList()
                .stream()
                .collect(groupingBy(
                        Town::getFortificationType,
                        () -> new EnumMap<>(FortificationType.class),
                        counting()));
    }

    /**
     * Faster cycle algorythm
     */
    @Transactional(readOnly = true)
    public Map<FortificationType, Integer> getTownFortificationStatsCYCLE(Long playerId) {
        var player = getPlayer(playerId);
        var townList = player.getTownsList();
        var map = new EnumMap<FortificationType, Integer>(FortificationType.class);
        for (Town town : townList) {
            map.merge(town.getFortificationType(), 1, Integer::sum);
        }
        return map;
    }

    public Player getPlayer(Long playerId) {
        return playerRepository
                .findById(playerId)
                .orElseThrow(getExceptionSupplier(
                        Player.class,
                        playerId,
                        EntityNotFoundException::new, false));
    }

    @SneakyThrows
    public String createTown(TownReqDto dto) {
        var town = townMapper.toEntity(dto);
        townRepository.save(town);
        return MessageProcessor
                .getReturnMessage(town, "townName");
    }

    public TownRespDto fetchTown(String name) {
        var town = getTown(name);
        return townMapper.toDto(town);
    }

    @Transactional
    public String allocateVisitingHero(String heroId, String townName) {
        var town = getTown(townName);
        var incomingHero = getHero(heroId);
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
    public Hero getHero(String heroCode) {
        return heroRepository.findByCode(heroCode)
                .orElseThrow(getExceptionSupplier(
                        Hero.class,
                        heroCode,
                        EntityNotFoundException::new, false));
    }

    @Transactional
    public List<TownGenerCreaturesReport> generateAllTownsWeeklyCreatures(Long playerId) {
        var player = getPlayer(playerId);
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
            var creatureOpt = getCreatureFromDwelling(dwellingName);
            if (creatureOpt.isEmpty()) continue;
            var creature = creatureOpt.get();
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
        var hero = getHero(dto.heroId());
        var heroArmy = hero.getArmyList();
        if (heroArmy.size() == ARMY_SLOT_MAX_QTY) {
            throw new ArmyFreeSlotOverflowException("No free slots for hiring creatures");
        }
        var playerResourcesMap = player.getResourceMap();
        var availCreaturesMap = getAvailCreaturesForHire(townName)
                .generCreatureMap();
        var newSlotsList = new ArrayList<CreatureSlot>();
        for (Map.Entry<CreatureType, Integer> entry : reqMap.entrySet()) {
            var creatureType = entry.getKey();
            var reqQty = entry.getValue();
            if (availCreaturesMap.containsKey(creatureType)) {
                var availCreatureQty = availCreaturesMap.get(creatureType);
                var creatureResourcePriceMap = getCreatureResourceMapFromCreatureType(
                        (creatureType.toString()));
                if (creatureResourcePriceMap == null || creatureResourcePriceMap.isEmpty()) {
                    throw new IllegalArgumentException("Creature Resource Map is null or empty, reinstate one before hire");
                }
                if (reqQty > availCreatureQty) {
                    log.error("{} is only {} left, while you ask {}", creatureType, availCreatureQty, reqQty);
                } else {
                    var updatedPlayerResourceMap = checkResourcesDemandsAndConsumeIfMet(
                            creatureResourcePriceMap,
                            playerResourcesMap);
                    var newSlot = new CreatureSlot();
                    newSlot.setType(creatureType);
                    newSlot.setQuantity(reqQty);
                    newSlotsList.add(newSlot);
                    var dwellingMap = town.getDwellingMap();
                    var dwellingName = AbstractDwellingTypeRegistry
                            .findDwellingByCreatureType(creatureType);
                    dwellingMap.put(dwellingName.getCode(), availCreatureQty - reqQty);
                    town.setDwellingMap(dwellingMap);
                    if (!updatedPlayerResourceMap.isEmpty()) {
                        playerResourcesMap.putAll(updatedPlayerResourceMap);
                        player.setResourceMap(playerResourcesMap);
                    }
                }
            } else {
                throw new HireCreatureException(creatureType + " is not available for hire in " + town.getName());
            }
        }
        heroArmy.addAll(newSlotsList);
        armyHeroService.syncArmySkillsWithHero(newSlotsList, hero);
        return newSlotsList;
    }

    private static Map<ResourceType, Integer> checkResourcesDemandsAndConsumeIfMet(
            Map<ResourceType, Integer> reqResourcesMap,
            Map<ResourceType, Integer> availResourceMap) {
        var updatedResourceMap = new EnumMap<ResourceType, Integer>(ResourceType.class);
        for (Map.Entry<ResourceType, Integer> reqEntry : reqResourcesMap.entrySet()) {
            var resourceName = reqEntry.getKey();
            var reqResQty = reqEntry.getValue();
            var availResQty = availResourceMap.get(resourceName);
            if (availResQty < reqResQty) throw new InsufficientResourcesException("No enough "
                    + resourceName + ": available : " + availResQty + ", while required " + reqResQty);
            else {
                updatedResourceMap.put(resourceName, availResQty - reqResQty);
            }
        }
        log.info("   ---> Previous resources MAP: {}", availResourceMap);
        log.info("   ---> Updated resources MAP: {}", updatedResourceMap);
        return updatedResourceMap;
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

    private Optional<Creature> getCreatureFromDwelling(String dwellingName) {
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
            var creatureOpt = getCreatureFromDwelling(dwellingName);
            if (creatureOpt.isEmpty()) continue;
            var creature = creatureOpt.get();
            var creatureGrowth = retrieveCreatureGrowth(creature);
            var hordeCreatureBoost = 0;
            if (!townHordeBuildingList.isEmpty()) {
                hordeCreatureBoost = getHordeBuildingCreatureBoost
                        (creature, townHordeBuildingList);
            }
            int grailModifiedValue = increaseCreatureBoostIfSpecificGrailBuilt(town, creature.getCreatureType());
            int modifiedValue = creatureGrowth + hordeCreatureBoost + grailModifiedValue;
            entry.setValue(entry.getValue() + modifiedValue);
            reportMap.put(creature.getCreatureType(), modifiedValue);
            townDwellingMap.put(entry.getKey(), entry.getValue());
            reportMap = getValueSortedMap(reportMap);
        }
        return new TownGenerCreaturesReport
                (town.getName(), reportMap);
    }
    private static int increaseCreatureBoostIfSpecificGrailBuilt(Town town, CreatureType creatureType) {
        if (town.getFaction() != Faction.INFERNO)
            return 0;
        if (!town.getUniqueBuildingSet().contains(GrailBuildingType.DEITY_OF_FIRE.name()))
            return 0;
        if (creatureType == IMP || creatureType == FAMILIAR)
            return 15;
        return 0;
    }

    @Nonnull
    private LinkedHashMap<CreatureType, Integer> getValueSortedMap(
            HashMap<CreatureType, Integer> reportMap) {
        return reportMap.entrySet()
                .stream()
                .sorted(Map.Entry
                        .comparingByValue(Comparator.reverseOrder()))
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
            var creatureTypesSet = horde.getCreatureTypes();
            if (creatureTypesSet == null) {
                log.warn("CreatureTypesSet is null, skipping");
                continue;
            }
            if (creatureTypesSet.contains(creature.getCreatureType())) {
                var hordeCreature = creatureTypesSet
                        .stream()
                        .filter(creat -> creat
                                .equals(creature.getCreatureType()))
                        .findAny()
                        .orElseThrow();
                return hordeCreature.creatureBoost();
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

    public CreatureType[] getAllTownCreaturesTypes
            (String townName, boolean essential) {
        var town = getTown(townName);
        var faction = town.getFaction();
        return CreatureTypeRegistry.getAllCreaturesByFaction(faction, essential);
    }

    public List<TownRespDto> createRandomTowns(int qty) {
        return Stream.generate(this::createRandomTown).limit(qty).map(townMapper::toDto).toList();
    }
    private Town createRandomTown() {
        var town = new Town();
        town.setName(IdGenerator.generateName());
        var alignment = IdGenerator.getRandomEnum(Alignment.class);
        town.setAlignment(alignment);
        var alignmentFactionsArray = alignment.getFaction().toArray();
        var randomFaction = (Faction) alignmentFactionsArray
                [IdGenerator.getThreadLocalRandomIndex(0, alignmentFactionsArray.length)];
        town.setFaction(randomFaction);
        var player = new Player();
        player.setName(IdGenerator.generateName());
        town.setPlayer(player);
        var dwellingMap = new HashMap<String, Integer>();
        var firstLevelDwelling = AbstractDwellingTypeRegistry
                .findDwellingByFaction(randomFaction, 1);
        dwellingMap.put(firstLevelDwelling.getCode(), 1);
        var secondLevelDwelling = AbstractDwellingTypeRegistry
                .findDwellingByFaction(randomFaction, 2);
        dwellingMap.put(secondLevelDwelling.getCode(), 2);
        town.setDwellingMap(dwellingMap);
        return town;
    }

    public TownGenerCreaturesReport generateWeeklyCreatures(String townName) {
        var town = getTown(townName);
        return retrieveTownDwellingsAndGenerateCreatures(town);
    }

    @Transactional(readOnly = true)
    public Set<String> getAvailTownsForTownPortal(String heroCode) {
        var hero = heroRepository.findByCode(heroCode).orElseThrow();
        var townList = hero.getPlayer().getTownsList();
        var secondarySkillMap = hero.getSecondarySkillMap();
        SkillLevel earthSchoolMagicLevel = SkillLevel.BASIC;
        if (secondarySkillMap == null
                || secondarySkillMap.isEmpty()
                || !secondarySkillMap.containsKey(SecondarySkill.EARTH_MAGIC)) {
            log.error("Secondary skill map is null, empty or " +
                    "doesn't contain dedicated spell");
            //do NOT check the spell availability,
            // this would be made on spell casting endpoint
        } else {
            earthSchoolMagicLevel = secondarySkillMap
                    .get(SecondarySkill.EARTH_MAGIC);
        }
        if (earthSchoolMagicLevel.ordinal() < 1)
            return Set.of(nearestUnoccupiedTown(hero, townList));
        else {
            return getAllUnoccupiedTowns(townList);
        }
    }

    private Set<String> getAllUnoccupiedTowns(List<Town> townList) {
        return townList.stream().filter(TownService::isUnoccupied)
                .map(Town::getName).collect(Collectors.toSet());
    }

    private String nearestUnoccupiedTown(Hero hero, List<Town> townList) {
        return townList
                .stream()
                .filter(TownService::isUnoccupied)
                .findFirst()  //calculate the nearestTown instead
                .orElseThrow()
                .getName();

    }

}
