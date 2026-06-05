package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.heroes.HeroParams;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.domain.spells.enums.TownBannedSpells;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.GrailBuildingType;
import ua.hudyma.exception.MethodNotImplementedException;
import ua.hudyma.exception.RequiredBuildingMissingException;
import ua.hudyma.service.TownService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.hudyma.domain.heroes.enums.SecondarySkill.NECROMANCY;
import static ua.hudyma.domain.heroes.enums.SkillLevel.CUSTOM;
import static ua.hudyma.domain.towns.enums.GrailBuildingType.AURORA_BOREALIS;

@Service
@RequiredArgsConstructor
@Log4j2
public class GrailBuildingService {

    private final TownService townService;
    //private final RandomService randomService;

    public void build(GrailBuildingType grailType, Town town) {
        switch (grailType) {
            case AURORA_BOREALIS -> buildConflux(town);
            case SPIRIT_GUARDIAN -> buildRampart(town);
            case SOUL_PRISON -> buildNecropolis(town);
            case DEITY_OF_FIRE -> buildInferno(town);
            case CARNIVOROUS_PLANT,
                 SPIRITS_OF_THE_FOREBEARS,
                 LODESTAR,
                 SKYSHIP,
                 COLOSSUS,
                 GUARDIAN_OF_EARTH,
                 LIGHTNING_ROD,
                 WARLORDS_MONUMENT -> throw new MethodNotImplementedException("Try later");
        }
        ;
    }
    private void buildInferno(Town town) {

    }

    private void buildNecropolis(Town town) {
        var player = town.getPlayer();
        var heroes = player.getHeroList();
        heroes
                .stream()
                .filter(hero -> hero.getSecondarySkillMap().containsKey(NECROMANCY))
                .forEach(hero -> hero.getSecondarySkillMap()
                        .put(NECROMANCY, CUSTOM));

        //apply secondarySkillCoefficient for SecSkill calculation
        /*final int secondarySkillCoefficient = NECROMANCY
                .getSkillLevelModifiers()[CUSTOM.ordinal()];*/
    }

    private void buildRampart(Town town) {
        var player = town.getPlayer();
        var heroes = player.getHeroList();
        heroes.forEach(hero ->
                hero.getParametersMap().merge(HeroParams.LUCK, 2, Integer::sum)
        );
        log.info("All heroes' luck successfully boosted 2 point up");
    }
    /**
     * For main part of grail-building refer to <b>build</b> method in {@link CommonBuildService}
     */

    private void buildConflux(Town town) {
        var commonBuildingMap = town.getCommonBuildingMap();
        if (!commonBuildingMap.containsKey(CommonBuildingType.MAGE_GUILD)) {
            throw new RequiredBuildingMissingException
                    ("Grail gives no benefit until you build a mage guild");
        }
        var mageGuildLevel = commonBuildingMap.get(CommonBuildingType.MAGE_GUILD);
        var allSpellsLimitedByLevelMap = getAllSpellsMapForSpecificLevel(mageGuildLevel, town);
        var magicGuildSpellMap = town.getMagicGuildSpellMap();
        if (magicGuildSpellMap == null)
            magicGuildSpellMap = new HashMap<>();
        magicGuildSpellMap.putAll(allSpellsLimitedByLevelMap);
        log.info(magicGuildSpellMap);
        log.info("{} successfully built in {}", AURORA_BOREALIS, town.getName());
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

    private Map<Integer, Set<String>> getAllSpellsMapForSpecificLevel(Integer mageGuildLevel, Town town) {
        var spellsMap = new HashMap<Integer, Set<String>>();
        var townBannedspellSet = getTownBannedSpellSet(town);
        while (mageGuildLevel > 0) {
            var specificLevelSpellSet = SpellRegistry
                    .resolveAllLevelSpells(mageGuildLevel)
                    .stream()
                    .filter(bannedSpell -> !townBannedspellSet.contains(bannedSpell))
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
            spellsMap.put(mageGuildLevel--, specificLevelSpellSet);
        }
        return spellsMap;
    }

    public Integer calculateGrailSpellDamage(String townName) {
        var town = townService.getTown(townName);
        var buildingQty =
                town.getCommonBuildingMap().size() +
                town.getUniqueBuildingSet().size() +
                town.getDwellingMap().size() +
                town.getHordeBuildingSet().size();
        var result = (35 + buildingQty) * 7;
        return Math.min(result, 168);
    }

}
