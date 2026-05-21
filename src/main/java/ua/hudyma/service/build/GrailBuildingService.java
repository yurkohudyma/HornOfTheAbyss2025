package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.domain.spells.enums.TownBannedSpells;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.GrailBuildingType;
import ua.hudyma.enums.Faction;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.MethodNotImplementedException;
import ua.hudyma.service.RandomService;
import ua.hudyma.service.TownService;

import java.util.*;
import java.util.stream.Collectors;

import static ua.hudyma.domain.towns.enums.GrailBuildingType.AURORA_BOREALIS;

@Service
@RequiredArgsConstructor
@Log4j2
public class GrailBuildingService {

    private final TownService townService;
    private final RandomService randomService;

    public String buildGrailBuilding(GrailBuildingType grailType, String townName) {
     return switch (grailType){
         case AURORA_BOREALIS -> buildConflux (townName);
         case CARNIVOROUS_PLANT,
              LODESTAR,
              SKYSHIP,
              SOUL_PRISON,
              SPIRIT_GUARDIAN,
              COLOSSUS,
              GUARDIAN_OF_EARTH,
              DEITY_OF_FIRE,
              LIGHTNING_ROD,
              WARLORDS_MONUMENT -> throw new MethodNotImplementedException("Try later");
     };
    }

    /**
     * Synthetic grail-building implementation <br>
     * For real one refer to <b>build</b> method in {@link CommonBuildService} <br>
     * However current method implements grail-benefits invokation while mentioned above - does not.
     */
    //@Transactional
    private String buildConflux(String townName) {
        var town = new Town();
        if (townName.isEmpty())
            town = randomService.createRandomTown(Faction.CONFLUX);
        else
            town = townService.getTown(townName);
        if (town.getUniqueBuildingSet().contains(AURORA_BOREALIS)){
            throw new BuildingAlreadyExistsException
                    ("Grail building " + AURORA_BOREALIS + " already exists in " +  townName);
        }
        var commonBuildingMap = town.getCommonBuildingMap();
        if (!commonBuildingMap.containsKey(CommonBuildingType.MAGE_GUILD)){
            return "Grail gives no benefit until you build a mage guild";
        }
        var mageGuildLevel = commonBuildingMap.get(CommonBuildingType.MAGE_GUILD);
        var allSpellsLimitedByLevelMap = getAllSpecificLevelSpellsMap(mageGuildLevel, town);
        var magicGuildSpellMap = town.getMagicGuildSpellMap();
        if (magicGuildSpellMap == null)
            magicGuildSpellMap = new HashMap<>();
        magicGuildSpellMap.putAll(allSpellsLimitedByLevelMap);
        town.getUniqueBuildingSet().add(AURORA_BOREALIS.name());
        System.out.println(magicGuildSpellMap);
        return AURORA_BOREALIS + " successfully built in " + townName;
    }
    private static List<String> getTownBannedSpellSet(Town town) {
        return TownBannedSpells
                .valueOf(town
                        .getFaction()
                        .toString())
                .getBannedSpellsSet().stream()
                .map(String::valueOf)
                .toList();
    }
    private Map<Integer, Set<String>> getAllSpecificLevelSpellsMap(Integer mageGuildLevel, Town town) {
        var spellsMap = new HashMap<Integer, Set<String>>();
        var townBannedspellSet = getTownBannedSpellSet(town);
        while (mageGuildLevel > 0) {
            var specificLevelSpellSet = SpellRegistry
                    .resolveAllLevelSpells(mageGuildLevel)
                    .stream()
                    .filter(bannedSpell -> !townBannedspellSet.contains(bannedSpell))
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
            spellsMap.put(mageGuildLevel, specificLevelSpellSet);
            --mageGuildLevel;
        }
        return spellsMap;
    }

}
