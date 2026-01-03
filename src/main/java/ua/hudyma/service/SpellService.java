package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.heroes.enums.SecondarySkill;
import ua.hudyma.domain.heroes.enums.SkillLevel;
import ua.hudyma.domain.spells.AbstractSpellSchool;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.domain.spells.enums.*;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.exception.RequiredBuildingMissingException;
import ua.hudyma.exception.SpellCastException;
import ua.hudyma.exception.SpellPointsShortageException;
import ua.hudyma.exception.TownSpellBookSetAlreadyGeneratedException;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static ua.hudyma.domain.heroes.HeroParams.CUR_SPELL_POINTS;
import static ua.hudyma.domain.heroes.enums.SecondarySkill.*;
import static ua.hudyma.domain.towns.enums.CommonBuildingType.MAGE_GUILD;

@Service
@RequiredArgsConstructor
@Log4j2
public class SpellService {
    private final TownService townService;
    private final HeroService heroService;
    private final SecureRandom random = new SecureRandom();
    private static final Map<Integer, Integer> SPELL_QTY_PER_LEVEL = Map.of(
            1, 5,
            2, 4,
            3, 3,
            4, 2,
            5, 1
    );
    @Transactional
    public String castSpell(String heroId, String spellName) {
        var hero = heroService.getHero(heroId);
        var spellBook = hero.getSpellBook();
        var enumSchool = SpellRegistry.fromCode(spellName);
        checkSpellBookContainsSpell(spellName, spellBook, hero.getName());
        var manaCost = enumSchool.getManaCost();
        var enumProperty = SpellRegistry.fromCodeProperty(spellName);
        var parametersMap = hero.getParametersMap();
        if (parametersMap == null) throw new IllegalStateException("Parameters_map is NULL");
        var currentSpellPoints = parametersMap.get(CUR_SPELL_POINTS);
        var manaCostModifier = getSecondarySkillManaCostModifier
                (       enumSchool.getSpellLevel(),
                        hero.getSecondarySkillMap(),
                        enumProperty.getSpellSchool());
        int spellPointsLeft = currentSpellPoints - manaCost - manaCostModifier;
        if (spellPointsLeft < 0) throw new SpellPointsShortageException
                (hero.getName() + "'s spell points = " + currentSpellPoints + ", " +
                        "while spell cost = " + manaCost);
        parametersMap.put(CUR_SPELL_POINTS, spellPointsLeft);


        //var skillModifierMap = enumProperty.getSkillModifierMap(); //todo if spell modifies hero Primary Skills
        //var targetCreatureSet = enumProperty.getTargetCreatureSet(); //todo implem these to provide creatures impact
        //var spellAction = enumSchool.getSpellAction(); //todo differentiate spell activity scope

        return "Spell " + spellName + " HAS been succ cast";
    }

    private int getSecondarySkillManaCostModifier(
            int spellLevel,
            Map<SecondarySkill, SkillLevel> secondarySkillMap,
            SpellSchool spellSchool) {
        int manaModifier = 0;
        var secondarySkillName = convertSpellSchoolToSecondarySkill
                (spellSchool);
        if (secondarySkillMap.containsKey(secondarySkillName)){
            return ++manaModifier * spellLevel;
        }
        return manaModifier;
    }

    private static SecondarySkill convertSpellSchoolToSecondarySkill(
            SpellSchool spellSchool) {
        return switch (spellSchool){
            case AIR -> AIR_MAGIC;
            case FIRE -> FIRE_MAGIC;
            case EARTH -> EARTH_MAGIC;
            case WATER -> WATER_MAGIC;
        };
    }

    private static void checkSpellBookContainsSpell(
            String spellName,
            Map<Integer, Set<String>> spellBook,
            String heroName) {
        for (Map.Entry<Integer, Set<String>> entry : spellBook.entrySet()){
            var spellSet = entry.getValue();
            for (String spell : spellSet) {
                if (spellSet.contains(spell)) return;
            }
            throw new SpellCastException(spellName + " HAS not been learnt by " + heroName + " yet");
        }
    }

    public Map<Integer, Set<String>> getHeroSpellbook(String heroId) {
        return heroService.getHero(heroId).getSpellBook();
    }

    public Map<Integer, Set<String>> getTownSpells(String townName) {
        return townService.getTown(townName).getMagicGuildSpellMap();
    }

    @Transactional
    public Map<Integer, Set<String>> learnHeroNewSpells(String heroId, String townName) {
        var hero = heroService.getHero(heroId);
        var town = townService.getTown(townName);
        if (hero.getSpellBook() == null){
            hero.setSpellBook(new HashMap<>());
        }
        var heroSecondarySkillMap = hero.getSecondarySkillMap();
        if (heroSecondarySkillMap == null || heroSecondarySkillMap.isEmpty()){
            throw new IllegalArgumentException("Hero secondary skill MAP is NULL or Empty");
        }
        var heroMaxSpellLevel = heroSecondarySkillMap
                .containsKey(SecondarySkill.WISDOM) ?
                getMaxSpellLevel(heroSecondarySkillMap.get(SecondarySkill.WISDOM)) : 2;
        var townSpells = town.getMagicGuildSpellMap();
        if (townSpells == null || townSpells.isEmpty()){
            throw new IllegalArgumentException("Town spell Book is NULL or empty");
        }
        while (heroMaxSpellLevel > 0) {
            var allowedSpells = townSpells.get(heroMaxSpellLevel);
            hero.getSpellBook().put(heroMaxSpellLevel--, allowedSpells);
        }
        return hero.getSpellBook();
    }

    private int getMaxSpellLevel(SkillLevel skillLevel) {
        return switch (skillLevel){
            case BASIC -> 3;
            case ADVANCED -> 4;
            case EXPERT -> 5;
        };
    }

    @Transactional
    public Set<String> randomiseSpellSet(String townName, int mageGuildLevel) {
        validateMageGuildLevel(mageGuildLevel);
        var town = townService.getTown(townName);
        validateMageGuildExists(town, mageGuildLevel);
        var spellMap = initOrValidateSpellMap(town, mageGuildLevel);
        var allLevelSpells = resolveAllLevelSpells(mageGuildLevel);
        int spellQty = SPELL_QTY_PER_LEVEL.get(mageGuildLevel);
        if (spellQty > allLevelSpells.size()) {
            throw new IllegalStateException(
                    "Not enough spells for Mage Guild level " + mageGuildLevel);
        }
        var generatedSpells = generateRandomSpells(allLevelSpells, spellQty);
        spellMap.put(mageGuildLevel, generatedSpells);
        return Set.copyOf(generatedSpells);
    }

    private void validateMageGuildLevel(int level) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Unsupported Mage Guild level: " + level);
        }
    }

    private void validateMageGuildExists(Town town, int level) {
        var buildings = town.getCommonBuildingMap();
        if (!buildings.containsKey(MAGE_GUILD) || buildings.get(MAGE_GUILD) < level) {
            throw new RequiredBuildingMissingException(
                    "Magic Guild of level " + level + " has NOT been built");
        }
    }

    private Map<Integer, Set<String>> initOrValidateSpellMap(Town town, int level) {
        if (town.getMagicGuildSpellMap() == null) {
            town.setMagicGuildSpellMap(new HashMap<>());
        }
        var spellMap = town.getMagicGuildSpellMap();
        if (spellMap.containsKey(level) && !spellMap.get(level).isEmpty()) {
            throw new TownSpellBookSetAlreadyGeneratedException(
                    "Spells for level " + level + " in " + town.getName() + " already generated");
        }
        return spellMap;
    }

    private Set<String> generateRandomSpells(List<String> spells, int qty) {
        Set<Integer> indexes = new HashSet<>();
        while (indexes.size() < qty) {
            indexes.add(random.nextInt(spells.size()));
        }
        return indexes.stream()
                .map(spells::get)
                .collect(Collectors.toSet());
    }

    private static List<String> resolveAllLevelSpells(int level) {
        var spells = new ArrayList<String>();
        spells.addAll(filterByLevel(EarthSpellSchool.values(), level));
        spells.addAll(filterByLevel(AirSpellSchool.values(), level));
        spells.addAll(filterByLevel(FireSpellSchool.values(), level));
        spells.addAll(filterByLevel(WaterSpellSchool.values(), level));
        return spells;
    }

    private static <T extends AbstractSpellSchool> List<String> filterByLevel(
            T[] spells, int level) {
        return Arrays.stream(spells)
                .filter(spell -> spell.getSpellLevel() == level)
                .map(String::valueOf)
                .toList();
    }
}
