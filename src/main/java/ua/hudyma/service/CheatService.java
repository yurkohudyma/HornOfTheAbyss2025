package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.artifacts.enums.ArtifactSlotDisposition;
import ua.hudyma.domain.cheats.enums.Cheats;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.HeroParams;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.enums.ArtifactSlot;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.resource.enums.ResourceType;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.converter.BuildingTypeResolver;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.FortificationType;
import ua.hudyma.domain.towns.enums.HallType;
import ua.hudyma.domain.towns.enums.HordeBuildingType;
import ua.hudyma.enums.Faction;
import ua.hudyma.enums.WarMachine;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.mapper.TownMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType.ARCHANGEL;
import static ua.hudyma.domain.creatures.enums.creaturetypes.NecropolisCreatureType.BLACK_KNIGHT;
import static ua.hudyma.domain.heroes.enums.ArtifactSlot.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class CheatService {
    private final HeroService heroService;
    private final SpellService spellService;
    private final TownMapper townMapper;
    private final int MAX_ARMY_SLOT_SIZE = 7;
    private final List<Cheats> cheatList = Arrays.stream(Cheats.values()).toList();
    private final HeroMapper heroMapper;

    @Transactional(readOnly = true)
    public Object engageCode(String cheatCode, String heroCode) {
        if (cheatCode.isEmpty() || cheatCodeIsUnknown(cheatCode)) {
            throw new IllegalArgumentException("Cheatcode" + cheatCode + " is not recognized");
        }
        var hero = heroService.getHero(heroCode);
        var player = hero.getPlayer();
        return switch (cheatCode) {
            case "nwctrinity" -> supplyTroops(hero, ARCHANGEL, 5);
            case "nwcagents" -> supplyTroops(hero, BLACK_KNIGHT, 10);
            case "nwctheconstruct" -> construct(player);
            case "nwcthereisnospoon" -> spoon(hero);
            case "nwczion" -> zion(player);
            case "nwcneo" -> neo(hero);
            case "nwclotsofguns" -> guns(hero);
            case "nwcfollowthewhiterabbit" -> rabbit(hero);
            case "nwcmorpheus" -> morpheus(hero);
            case "nwcarchitect" -> architect(hero);
            default -> throw new IllegalArgumentException("cheatcode " + cheatCode + " not implemented yet");
        };
    }

    private HeroRespDto morpheus(Hero hero) {
        hero.getParametersMap().replace(HeroParams.MORALE, 999);
        return heroMapper.toDto(hero);
    }

    private HeroRespDto architect(Hero hero) {
        throw new IllegalStateException("Method not impl");
    }

    private HeroRespDto rabbit(Hero hero) {
        hero.getParametersMap().replace(HeroParams.LUCK, 999);
        return heroMapper.toDto(hero);
    }

    private HeroRespDto guns(Hero hero) {
        var warMachineInventorySet = hero.getWarMachineInventorySet();
        if (warMachineInventorySet == null)
            warMachineInventorySet = EnumSet.noneOf(WarMachine.class);
        warMachineInventorySet.addAll(Set.of(WarMachine.CANNON, WarMachine.FIRST_AID_TENT, WarMachine.AMMO_CART));
        var miscInventoryMap = hero.getMiscInventoryMap();
        miscInventoryMap.putAll(Map.of(
                WARMACHINE, ArtifactSlotDisposition.CANNON,
                FIRST_AID, ArtifactSlotDisposition.FIRST_AID_TENT,
                AMMO, ArtifactSlotDisposition.AMMO_CART
        ));
        return heroMapper.toDto(hero);
    }

    private HeroRespDto neo(Hero hero) {
        hero.setLevel(hero.getLevel() + 1);
        return heroMapper.toDto(hero);
    }

    @Transactional(readOnly = true)
    private List<TownRespDto> zion(Player player) {
        var townList = player.getTownsList();
        for (Town town : townList){
            var commonBuildingMap = town.getCommonBuildingMap();
            var townFaction = town.getFaction();
            var allCommonBuildings = Arrays
                    .stream(CommonBuildingType.values())
                    .collect(Collectors
                            .toMap(Function.identity(),
                                    v -> 0,
                                    (o1, o2) -> o1,
                                    () -> new EnumMap<>(CommonBuildingType.class)));
            commonBuildingMap.putAll(allCommonBuildings);
            commonBuildingMap.replace(CommonBuildingType.MAGE_GUILD,
                    retrieveMageGuildMaxLevel(townFaction));
            town.setFortificationType(FortificationType.CASTLE);

            var hordeBuildingSet = town.getHordeBuildingSet();
            hordeBuildingSet.addAll(retrieveTownSpecificHordeBuildings(townFaction));

            var townSpecificDwellings = BuildingTypeResolver
                    .retrieveAllTownSpecificUpgradedDwellings(townFaction);
            town.setDwellingMap(townSpecificDwellings);
            //todo if there were any creatures they would be zeroed
            //todo so we need to transfer all existing for hire creatures into new dwellings


            town.setHallType(HallType.CITY_HALL);
            var townSpecificUniqueBuildings = BuildingTypeResolver
                    .retrieveAllTownSpecificUniqueBuildings(townFaction);
            town.setUniqueBuildingSet(townSpecificUniqueBuildings);
        }
        townList.get(0).setHallType(HallType.CAPITOL);
        return townMapper.toDtoList(townList);
    }

    private Set<String> retrieveTownSpecificHordeBuildings(Faction townFaction) {
        return Arrays
                .stream(HordeBuildingType.values())
                .filter(constant -> constant
                        .getFaction()
                        .equals(townFaction))
                .map(String::valueOf)
                .collect(Collectors.toSet());
    }

    private Integer retrieveMageGuildMaxLevel(Faction faction) {
        return faction.getMagicGuildMaxLevel();
    }

    //@Transactional
    // disabled for testing
    private HeroRespDto spoon(Hero hero) {
        var allSpellSet = spellService.getAllSpells();
        var allSpellMap = spellService
                .convertSpellSetIntoSpellMap(allSpellSet);
        var allSpellsSetMap = spellService
                .convertListSpellMapIntoSetSpellMap(allSpellMap);
        hero.setSpellBook(allSpellsSetMap);
        var parametersMap = heroService.getOrCreateHeroParamsMap(hero);
        parametersMap.put(HeroParams.CUR_SPELL_POINTS, 999);
        return heroMapper.toDto(hero);
    }

    //@Transactional
    // disabled for testing
    private Map<ResourceType, Integer> construct(Player player) {        
        var resourceMap = player.getResourceMap();
        resourceMap.merge(ResourceType.GOLD, 100_000, Integer::sum);
        resourceMap
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(ResourceType.GOLD))
                .forEach(entry ->
                        resourceMap
                        .merge(entry.getKey(),
                                100,
                                Integer::sum));
        return resourceMap;
    }

    //@Transactional
    // disabled for testing
    private List<CreatureSlot> supplyTroops(
            Hero hero,
            CreatureType creatureType,
            int quantity) {
        var heroArmy = hero.getArmyList();
        var vacantArmySize = MAX_ARMY_SLOT_SIZE - heroArmy.size();
        IntStream.range(0, Math.max(0, vacantArmySize))
                .forEach(i -> heroArmy.add(
                        new CreatureSlot(creatureType, quantity)
                ));
        return heroArmy;
    }

    private boolean cheatCodeIsUnknown(String cheatCode) {
        return !cheatList.contains(Cheats.valueOf(cheatCode.toLowerCase()));
    }
}
