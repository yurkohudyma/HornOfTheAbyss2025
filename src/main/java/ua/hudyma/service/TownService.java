package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.creatures.Creature;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.converter.AbstractDwellingTypeRegistry;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.HordeBuildingType;
import ua.hudyma.dto.TownGenerCreaturesReport;
import ua.hudyma.enums.Faction;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.repository.TownRepository;
import ua.hudyma.util.MessageProcessor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

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

    private TownGenerCreaturesReport retrieveTownDwellingsAndGenerateCreatures(Town town) {
        var townDwellingMap = town.getDwellingMap();
        if (townDwellingMap == null) throw new IllegalArgumentException("Dwelling map for town "
                + town.getName() + " has not been created YET");
        var townHordeBuildingList =
                retrieveHordeBuildingTypeByFaction(town.getFaction());
        var reportMap = new HashMap<CreatureType, Integer>();
        for (Map.Entry<String, Integer> entry : townDwellingMap.entrySet()){
            var dwellingName = entry.getKey();
            var specificDwellingEnum = AbstractDwellingTypeRegistry
                    .fromCode(dwellingName);
            var creatureEnum = specificDwellingEnum.getCreature();
            Creature creature = null;
            try {
                creature = creatureService.fetchCreatureByType(creatureEnum);
            } catch (EntityNotFoundException e) {
                log.error("Entity {} not found", creatureEnum);
            }
            if (creature == null) continue;
            var creatureGrowth = retrieveCreatureGrowth(creature);
            var hordeCreatureBoost = 0;
            if (!townHordeBuildingList.isEmpty()) {
                hordeCreatureBoost = getHordeBuildingCreatureBoost(creature, townHordeBuildingList);
            }
            int modifiedValue = creatureGrowth + hordeCreatureBoost;
            entry.setValue(entry.getValue() + modifiedValue);
            reportMap.put(creature.getCreatureType(), modifiedValue);
            townDwellingMap.put(entry.getKey(), entry.getValue());
            reportMap = getValueSortedMap(reportMap);

        }
        return new TownGenerCreaturesReport(town.getName(), reportMap);
    }

    //todo implement hiring creatures

    @Nonnull
    private static LinkedHashMap<CreatureType, Integer> getValueSortedMap(
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
        for (HordeBuildingType horde : townHordeBuildingList){
            if (horde.getCreatureType() == creature.getCreatureType()){
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
