package ua.hudyma.service;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.artifact.ArtifactProperties;
import ua.hudyma.domain.artifact.ArtifactSlotDisposition;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.HeroParams;
import ua.hudyma.domain.heroes.dto.*;
import ua.hudyma.domain.heroes.enums.*;
import ua.hudyma.dto.WarMachineRespDto;
import ua.hudyma.enums.WarMachine;
import ua.hudyma.exception.ArtifactAlreadyAttachedException;
import ua.hudyma.exception.ArtifactFreeSlotMissingException;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.repository.HeroRepository;
import ua.hudyma.util.FixedSizeMap;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static ua.hudyma.domain.creatures.enums.CreatureSkill.*;
import static ua.hudyma.domain.heroes.HeroParams.*;
import static ua.hudyma.domain.heroes.enums.ArtifactSlot.*;
import static ua.hudyma.domain.heroes.enums.HeroSpecialtyType.SECONDARY_SKILL;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.KNOWLEDGE;
import static ua.hudyma.domain.heroes.enums.SecondarySkill.*;
import static ua.hudyma.enums.WarMachineProperties.*;
import static ua.hudyma.enums.WarMachineProperties.CATAPULT;
import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;
import static ua.hudyma.util.MessageProcessor.getReturnMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class HeroService {

    private static final int MISC_INV_MAP_SIZE = 5;

    private static final Set<ArtifactSlot> MISC_SLOTS_SET = Set.of(MISC_A, MISC_B, MISC_C, MISC_D, MISC_E);

    private static final Map<Integer, Integer> LOWEST_CREATURE_BASE_MOVEMENT_VALUE_MAP =
            Map.ofEntries(
                    Map.entry(0, 1300),
                    Map.entry(1, 1360),
                    Map.entry(2, 1430),
                    Map.entry(3, 1500),
                    Map.entry(4, 1560),
                    Map.entry(5, 1630),
                    Map.entry(6, 1700),
                    Map.entry(7, 1760),
                    Map.entry(8, 1830),
                    Map.entry(9, 1900),
                    Map.entry(10, 1960),
                    Map.entry(11, 2000)
            );

    private static final int[] HERO_LEVEL_EXPERIENCE_ARRAY = new int[]{
            //https://heroes.thelazy.net/index.php/Experience
            0,          //0
            0,          //1
            1_000,      //2
            2_000,      //3
            3_200,      //4
            4_600,      //5
            6_200,      //6
            8_000,      //7
            10_000,     //8
            12_200,     //9
            14_700,     //10
            17_500,
            20_600,
            24_320,
            28_784,
            34_140,
            40_567,
            48_279,
            57_533,
            68_637,
            81_961,     //20
            97_949,
            117_134,
            140_156,
            167_782,
            200_933,
            240_714,
            288_451,
            345_735,
            414_475,
            496_963,    //30
            595_948,
            714_730,
            857_268,
            1_028_313,
            1_233_567,
            1_479_871,
            1_775_435,
            2_130_111,
            2_555_722,
            3_066_455,  //40
            3_679_334,
            4_414_788,
            5_297_332,
            6_356_384,
            7_627_246,
            9_152_280,
            10_982_320,  //47
            13_178_368,
            15_813_625,
            18_975_933,  //50
            22_770_702,
            27_324_424,
            32_788_890,
            39_346_249,
            47_215_079,  //55
            56_657_675,
            67_988_790,
            81_586_128,
            97_902_933,
            117_483_099, //60
            140_979_298,
            169_174_736,
            203_009_261,
            243_610_691,
            292_332_407, //65
            350_798_466,
            420_957_736,
            505_148_860,
            606_178_208,
            727_413_425, //70
            872_895_685,
            1_047_474_397,
            1_256_968_851,
            1_508_362_195,
            1_810_034_207, //75
    };

    private final HeroMapper heroMapper;

    private final HeroRepository heroRepository;

    private final PlayerService playerService;

    private final ArmyHeroService armyHeroService;

    private final HeroCreatureService heroCreatureService;

    @SneakyThrows
    @Transactional
    public String createHero(HeroReqDto dto) {
        var hero = heroMapper.toEntity(dto);
        var player = playerService.getPlayer(dto.playerId());
        hero.setPlayer(player);
        heroRepository.save(hero);
        return getReturnMessage(hero, "townName");
    }

    @Transactional
    public HeroRespDto defPriSkillsEmptyBodyInvMapAndParamMap(String heroId) {
        var hero = getHero(heroId);
        var skillMap = hero.getPrimarySkillMap();
        for (Map.Entry<PrimarySkill, Integer> entry : skillMap.entrySet()) {
            entry.setValue(0);
        }
        hero.setBodyInventoryMap(Map.of());
        hero.setParametersMap(Map.of());
        return heroMapper.toDto(hero);
    }

    public HeroRespDto fetchHero(String code) {
        var hero = getHero(code);
        return heroMapper.toDto(hero);
    }

    public List<HeroRespDto> fetchHeroDtoList(List<Hero> heroList) {
        return heroMapper.toDtoList(heroList);
    }

    public Hero getHero(String heroCode) {
        return heroRepository.findByCode(heroCode)
                .orElseThrow(getExceptionSupplier(
                        Hero.class,
                        heroCode,
                        EntityNotFoundException::new,
                        false));
    }

    //@Transactional disabled for testing

    public HeroExperienceRespDto sacrificeArtifactsForExperience(SacrificeArtifactsReqDto dto) {
        var hero = getHero(dto.heroId());
        var artifactsArray = dto.artifactsArray();
        var currentLevel = hero.getLevel();
        var currentExperience = hero.getExperience();
        var gainedExperience = calculateSacrificedArtifactsExp(hero, artifactsArray);
        var nextLevel = calculateHeroNextExpLevel(gainedExperience);
        PrimarySkill gainedPrimarySkill = null;
        int currentPrimarySkillValue;
        if (nextLevel > currentLevel) {
            gainedPrimarySkill = gainNewLevel(hero, gainedExperience, nextLevel);
        }
        currentPrimarySkillValue = hero.getPrimarySkillMap().get(gainedPrimarySkill);
        return new HeroExperienceRespDto(
                hero.getName(),
                currentLevel,
                nextLevel,
                currentExperience,
                gainedExperience,
                gainedPrimarySkill,
                currentPrimarySkillValue - 1,
                currentPrimarySkillValue);
    }
    private static PrimarySkill gainNewLevel(Hero hero, Integer gainedExperience, int nextLevel) {
        var heroStartingPrimarySkillValues = hero.getSubfaction().getPrimarySkillValues();
        var primarySkillPercentageValues = calculatePercentageValues(heroStartingPrimarySkillValues);
        hero.setLevel(nextLevel);
        hero.setExperience(gainedExperience);
        var gainedPrimarySkill = randomisePrimarySkill(primarySkillPercentageValues);
        var primarySkillMap = getOrCreatePrimarySkillMap(hero);
        var skillValue = primarySkillMap.get(gainedPrimarySkill);
        primarySkillMap.replace(gainedPrimarySkill, ++skillValue);
        return gainedPrimarySkill;
    }

    private static PrimarySkill randomisePrimarySkill(int[] primaryArray) {
        var map = new HashMap<PrimarySkill, List<Integer>>();
        var values = PrimarySkill.values();
        var modifier = 0;
        for (int i = 0; i < primaryArray.length; i++) {
            map.put(values[i], List.of(modifier, modifier + primaryArray[i] - 1));
            modifier += primaryArray[i];
        }
        var randomValue = ThreadLocalRandom.current().nextInt(100);
        return map
                .entrySet()
                .stream()
                .filter(entry -> entry
                        .getValue().get(0) <= randomValue && entry.getValue().get(1) > randomValue)
                .findAny()
                .orElseThrow()
                .getKey();

        //40%, 10%, 30%, 20%

        //att 0..39   (+40)
        //def 40..49  (+10)
        //pow 50..79  (+30)
        //kno 80..100 (+20)
    }

    private static Map<PrimarySkill, Integer> getOrCreatePrimarySkillMap(Hero hero) {
        var primarySkillMap = hero.getPrimarySkillMap();
        if (primarySkillMap == null) {
            primarySkillMap = new EnumMap<>(PrimarySkill.class);
            hero.setPrimarySkillMap(primarySkillMap);
        }
        return primarySkillMap;
    }

    private static int[] calculatePercentageValues(int[] heroStartingPrimarySkillValues) {
        var maxElement = getMaxElement(heroStartingPrimarySkillValues);

        //якщо є елементи зі знач 0, то потрібно інкрементувати весь масив на 1
        if (Arrays
                .stream(heroStartingPrimarySkillValues)
                .anyMatch(a -> a == 0)) {
            incrementArrayByOne(heroStartingPrimarySkillValues);
        }
        var arrayElementsSum = getArrayElementsSum(heroStartingPrimarySkillValues);
        var percentageArray = Arrays
                .stream(heroStartingPrimarySkillValues)
                .mapToDouble(ele -> ele / (double) arrayElementsSum * 100)
                .mapToInt(value -> (int) value)
                .toArray();
        var maxPercentageElement = getMaxElement(percentageArray);

        //якщо сума всіх відсотків не 100%, додати 1 до найбільшого (якшо найбільших два, тоді додати до обох)
        if (getArrayElementsSum(percentageArray) < 100) {
            for (int i = 0; i < percentageArray.length; i++) {
                if (percentageArray[i] == maxPercentageElement) {
                    ++percentageArray[i];
                }
            }
        }
        return percentageArray;
    }

    private static int getArrayElementsSum(int[] heroStartingPrimarySkillValues) {
        return Arrays.stream(heroStartingPrimarySkillValues).sum();
    }

    private static int[] incrementArrayByOne(int[] heroStartingPrimarySkillValues) {
        for (int i = 0; i < heroStartingPrimarySkillValues.length; i++) {
            ++heroStartingPrimarySkillValues[i];
        }
        return heroStartingPrimarySkillValues;
    }

    private static int getMaxElement(int[] array) {
        var maxElement = 0;
        for (int element : array) {
            if (element > maxElement) {
                maxElement = element;
            }
        }
        return maxElement;
    }

    public InventoryPresetDto saveEquipmentPreset(Integer number, String heroId) {
        //intentionally without DB persisting, in-memory storing only
        var hero = getHero(heroId);
        var bodyInventoryMap = getOrCreateBodyInvMap(hero);
        var miscInventoryMap = getOrCreateMiscInventoryMap(hero);
        InventoryPresetDto inventoryPresetDto;
        var inventoryPresetList = getOrCreateInventoryPresetList(hero);
        if (inventoryPresetList.size() > 8 && number > 8) {
            log.warn("Preset is full, overwriting from beginning");
            inventoryPresetDto = new InventoryPresetDto(0, bodyInventoryMap, miscInventoryMap);
        }
        else if (inventoryPresetList.size() == 0){
            log.warn("Preset is empty, starting from 0 index");
            inventoryPresetDto = new InventoryPresetDto(0, bodyInventoryMap, miscInventoryMap);
        }
        else {
            inventoryPresetDto = new InventoryPresetDto(number, bodyInventoryMap, miscInventoryMap);
        }
        inventoryPresetList.add(number, inventoryPresetDto);
        hero.setInventoryPresetList(inventoryPresetList);
        return inventoryPresetDto;
    }

    public InventoryPresetDto retrieveEquipmentPreset(Integer number, String heroId) {
        var hero = getHero(heroId);
        return hero.getInventoryPresetList().get(number);
    }

    private Integer calculateSacrificedArtifactsExp(Hero hero, String[] artifactsList) {
        var accumulatedExperience = 0;
        for (String artifact : artifactsList) {
            try {
                var artifactProp = ArtifactProperties.valueOf(artifact);
                var expirienceValue = artifactProp.getExpirienceValue();
                if (heroOwnsArtifact(hero, artifact)) {
                    accumulatedExperience += expirienceValue;
                } else log.error("Artifact {} does not belong to {}", artifact, hero.getName());
            } catch (IllegalArgumentException ignored) {
                log.error("Artifact {} not recognised. Skipping", artifact);
            }
        }
        return accumulatedExperience;
    }

    private int calculateHeroNextExpLevel(Integer gainedExp) {
        for (int i = 0; i < HERO_LEVEL_EXPERIENCE_ARRAY.length; i++) {
            if (gainedExp <= HERO_LEVEL_EXPERIENCE_ARRAY[i] && gainedExp > HERO_LEVEL_EXPERIENCE_ARRAY[i - 1]) {
                return i - 1;
            }
        }
        throw new IllegalStateException("Hero experience " + gainedExp + " exceeds level range of 75");
    }

    //todo gaining exp from battles
    //From winning a combat,
    // the hero receives one experience point for each HP of the enemy creatures
    // he managed to vanquish in the battle (clones, war machines and bonus health from artifacts do not count).
    // Resurrected creatures are only counted once, except Demons raised by Pit Lords.
    // If an enemy hero is defeated or an enemy town is captured during a siege,
    // the hero receives 500 extra experience points. A hero will not receive this bonus
    // if the enemy hero escapes or surrenders. If a hero escapes or surrenders,
    // then that hero does not gain any experience,
    // even if he/she managed to kill creatures from the victorious hero.
    // A hero from whom the enemy hero escaped receives experience for the killed enemy creatures,
    // but does not gain experience for the defeated hero.

    private boolean heroOwnsArtifact(Hero hero, String artifact) {
        var bodyInventoryMap = getOrCreateBodyInvMap(hero);
        var bodyInventory = stringifyArtifactSlotDispositionList(
                bodyInventoryMap.values());
        var backpackInventoryList = stringifyArtifactSlotDispositionList(
                getOrCreateBackpackInventoryList(hero));
        var miscInventory = stringifyArtifactSlotDispositionList(getOrCreateMiscInventoryMap(hero)
                .values());
        return backpackInventoryList.contains(artifact) ||
                miscInventory.contains(artifact) ||
                bodyInventory.contains(artifact);
    }

    private Collection<ArtifactSlotDisposition> getOrCreateBackpackInventoryList(Hero hero) {
        var backpackInventoryList = hero.getBackpackInventoryList();
        return backpackInventoryList == null ? new ArrayList<>() : backpackInventoryList;
    }

    @Nonnull
    private static List<String> stringifyArtifactSlotDispositionList(
            Collection<ArtifactSlotDisposition> inventory) {
        return inventory
                .stream()
                .map(String::valueOf)
                .toList();
    }

    @Transactional
    public HeroRespDto syncHeroSkillsUponArtifactAttachment
            (String heroId, String artifactName) {
        var hero = getHero(heroId);
        var artifact = ArtifactProperties.valueOf(artifactName);
        var artifactAction = artifact.getArtifactAction();
        var actionData = artifact.getActionData();
        Map<PrimarySkill, Integer> primarySkillsMap;
        Map<HeroParams, Integer> parametersMap = getOrCreateParamMap(hero);
        switch (artifactAction) {
            case BOOST -> {
                primarySkillsMap = hero.getPrimarySkillMap();
                for (Map.Entry<String, Object> entry : actionData.entrySet()) {
                    var skillName = entry.getKey();
                    if (skillName != null && !skillName.isEmpty()) {
                        var primarySkillEnum = PrimarySkill.valueOf(skillName);
                        var boostableValue = entry.getValue();
                        primarySkillsMap.computeIfPresent(primarySkillEnum,
                                (k, v) -> v + (Integer) boostableValue);
                        if (primarySkillEnum == KNOWLEDGE) {
                            syncSpellPointsValues(hero);
                        }
                    }
                }
            }
            case BOOST_OTH_PARAM -> {
                hero.setParametersMap(parametersMap);
                for (Map.Entry<String, Object> entry : actionData.entrySet()) {
                    var skillName = entry.getKey();
                    if (skillName != null && !skillName.isEmpty()) {
                        var boostableValue = entry.getValue();
                        var parameterEnum = HeroParams.valueOf(entry.getKey());
                        if (!parametersMap.containsKey(parameterEnum)) {
                            parametersMap.compute(parameterEnum,
                                    (k, v) -> (Integer) boostableValue);
                        } else {
                            parametersMap.computeIfPresent(parameterEnum, (k, v)
                                    -> v + (Integer) boostableValue);
                        }
                    }
                }
                syncSpellPointsValues(hero);
            }
            case COMPLEX, MODIFIER, ENEMY_DEBOOST -> throw new IllegalArgumentException
                    ("COMPLEX/ENEMY_BOOST/MODIFIER/VISIBILITY " +
                            "action in ARTIFACTS not supported");
            case ALL_AIR_SPELLS, ALL_EARTH_SPELLS, ALL_FIRE_SPELLS, ALL_WATER_SPELLS ->
                    log.info(" ::: attaching Tome of Magic");
            default -> throw new IllegalArgumentException
                    ("syncHeroSkillsUponArtifactAttachment : Case not implemented");
        }
        attachArtifactToHero(artifactName, hero);
        armyHeroService.syncArmySkillsWithHero(hero.getArmyList(), hero);
        return heroMapper.toDto(hero);
    }

    private static Map<HeroParams, Integer> getOrCreateParamMap(Hero hero) {
        return hero.getParametersMap() == null ?
                new EnumMap<>(HeroParams.class) :
                hero.getParametersMap();
    }

    @Transactional
    public HeroRespDto syncHeroSkillsUponArtifactDetachment(String heroId, String artifactName) {
        var hero = getHero(heroId);
        var artifactProperties = ArtifactProperties.valueOf(artifactName);
        var artifactPropMap = artifactProperties.getActionData();
        Map<PrimarySkill, Integer> heroSkillMap;
        Map<HeroParams, Integer> parametersMap;
        switch (artifactProperties.getArtifactAction()) {
            case BOOST -> {
                heroSkillMap = hero.getPrimarySkillMap();
                for (Map.Entry<String, Object> entry : artifactPropMap.entrySet()) {
                    var primarySkillEnum = PrimarySkill.valueOf(entry.getKey());
                    heroSkillMap.computeIfPresent(primarySkillEnum, (k, v) -> {
                        if (v <= 0) return v + Math.abs((Integer) entry.getValue());
                        else return (Integer) entry.getValue() - v;
                    });
                    if (primarySkillEnum == KNOWLEDGE) {
                        syncSpellPointsValues(hero);
                    }
                }
            }
            case BOOST_OTH_PARAM -> {
                parametersMap = hero.getParametersMap();
                if (parametersMap == null) throw new IllegalStateException("Param Map is NULL, cannot retrieve data");
                for (Map.Entry<String, Object> entry : artifactPropMap.entrySet()) {
                    var parameterEnum = HeroParams.valueOf(entry.getKey());
                    parametersMap.computeIfPresent(parameterEnum, (k, v) -> {
                        if (v <= 0) return v + Math.abs((Integer) entry.getValue());
                        else return (Integer) entry.getValue() - v;
                    });
                }
            }
            case COMPLEX, MODIFIER, ENEMY_DEBOOST, VISIBILITY -> log.info(" :::: do NOTHING");
            case ALL_AIR_SPELLS, ALL_EARTH_SPELLS, ALL_FIRE_SPELLS, ALL_WATER_SPELLS ->
                    log.info(" ::: detaching Tome of Magic");
        }
        detachArtifact(artifactName, hero);
        hero.setArmyList(armyHeroService.syncArmySkillsWithHero(hero));
        return heroMapper.toDto(hero);
    }

    public void syncSpellPointsValues(Hero hero) {
        Map<HeroParams, Integer> paramMap = getOrCreateHeroParamsMap(hero);
        var intelligenceLevel = getIntelligenceLevel(hero);
        var knowledgeLevel = getKnowledgeLevel(hero);
        paramMap.put(MAX_SPELL_POINTS, (int) (knowledgeLevel * 10 * intelligenceLevel));
        paramMap.putIfAbsent(CUR_SPELL_POINTS, paramMap.get(MAX_SPELL_POINTS));
    }

    public Map<HeroParams, Integer> getOrCreateHeroParamsMap(Hero hero) {
        var paramMap = hero.getParametersMap();
        if (paramMap == null) {
            paramMap = new FixedSizeMap<>(new HashMap<>(), 4);
        }
        return paramMap;
    }

    public LinkedList<InventoryPresetDto> getOrCreateInventoryPresetList(Hero hero) {
        var inventoryPresetList = hero.getInventoryPresetList();
        return inventoryPresetList == null ? new LinkedList<>() : inventoryPresetList;
    }

    static int getKnowledgeLevel(Hero hero) {
        var knowledgeLevel = hero.getPrimarySkillMap().get(KNOWLEDGE);
        return knowledgeLevel <= 0 ? 1 : knowledgeLevel;
    }

    static float getIntelligenceLevel(Hero hero) {
        var secondarySkillMap = hero.getSecondarySkillMap();
        var intel = secondarySkillMap.get(INTELLIGENCE);
        if (intel == null) return 1;
        return switch (intel) {
            case BASIC -> 1.2f;
            case ADVANCED -> 1.35f;
            case EXPERT -> 1.5f;
            case CUSTOM -> throw new IllegalArgumentException("Not applicable for " + INTELLIGENCE);
        };
    }

    @Transactional
    public MovemementPointsRespDto updateAndFetchHeroMovementPoints(String heroCode) {
        var hero = getHero(heroCode);
        var paramMap = getOrCreateHeroParamsMap(hero);
        var recalculatedMaxMovePoints = recalculateMaxMovePoints(hero);
        paramMap.putAll(
                Map.of(CUR_MOVE_POINTS, recalculatedMaxMovePoints[0],
                        MAX_MOVE_POINTS, recalculatedMaxMovePoints[1],
                        CUR_WATER_MOVE_POINTS, recalculatedMaxMovePoints[2],
                        MAX_WATER_MOVE_POINTS, recalculatedMaxMovePoints[3]));
        hero.setParametersMap(paramMap);
        return new MovemementPointsRespDto(
                recalculatedMaxMovePoints[0],
                recalculatedMaxMovePoints[1],
                recalculatedMaxMovePoints[2],
                recalculatedMaxMovePoints[3]);

        //https://heroes.thelazy.net/index.php/Movement

        /*Boots of Speed give +400 Horn of the Abyss points on land
        Equestrian's Gloves give +200 Horn of the Abyss points on land
        Necklace of Ocean Guidance gives +1000 points on water
        Sea Captain's Hat gives +500 points on water.*/
    }

    private int[] recalculateMaxMovePoints(Hero hero) {
        var landResult = 1500;
        var slowestCreatureSpeed = heroCreatureService.getHeroSlowestCreatureSpeedValue(hero);
        if (slowestCreatureSpeed < 0) throw new IllegalArgumentException("Slowest creature speed CANNOT be negative");
        landResult = slowestCreatureSpeed > 11 ? 2000 :
                LOWEST_CREATURE_BASE_MOVEMENT_VALUE_MAP.get(slowestCreatureSpeed);
        var waterResult = 2000;
        var heroMovementModifiers = Arrays
                .stream(HeroMovementModifiers.values()).toList();
        var bodyInventoryMap = getOrCreateBodyInvMap(hero);
        for (Map.Entry<ArtifactSlot, ArtifactSlotDisposition> entry : bodyInventoryMap.entrySet()) {
            var artifactSlot = entry.getKey();
            if (artifactSlot.getEntityField() != EntityField.BODY) continue;
            for (HeroMovementModifiers modifier : heroMovementModifiers) {
                if (entry.getValue().toString().equals(modifier.toString())) {
                    landResult += modifier.getMovePoints();
                }
            }
        }
        var secondarySkillMap = hero.getSecondarySkillMap();
        float logisticsModifier = 0, pathfindingModifier = 0, navigationModifier = 0;
        if (secondarySkillMap.containsKey(LOGISTICS)) {
            logisticsModifier = (float)
                    LOGISTICS
                            .getSkillLevelModifiers()
                            [getSecondarySkillModifierNumber(
                            secondarySkillMap.get(LOGISTICS))] / 100;
        }
        if (secondarySkillMap.containsKey(PATHFINDING)) {
            pathfindingModifier = (float)
                    PATHFINDING.getSkillLevelModifiers()
                            [getSecondarySkillModifierNumber(
                            secondarySkillMap.get(PATHFINDING))] / 100;
        }
        if (secondarySkillMap.containsKey(NAVIGATION)) {
            navigationModifier = (float)
                    NAVIGATION.getSkillLevelModifiers()
                            [getSecondarySkillModifierNumber(
                            secondarySkillMap.get(NAVIGATION))] / 100;
        }
        var heroSpecialtyModifier = calcSpecialtyModifier(hero.getCode());
        landResult += (int) (landResult * logisticsModifier);
        landResult += (int) (landResult * pathfindingModifier);
        waterResult += (int) (waterResult * navigationModifier);
        landResult += heroSpecialtyModifier;
        return new int[]{landResult, landResult, waterResult, waterResult};
    }

    @Transactional
    public Integer calcSpecialtyModifier(String heroCode) {
        var hero = getHero(heroCode);
        var heroSpecialty = hero.getHeroSpecialty();
        Object specialtyProperty;
        if (heroSpecialty == null || (specialtyProperty = heroSpecialty.property()) == null)
            return -1;
        var heroLevel = hero.getLevel();
        SkillLevel secondarySkillSpecialtyLevel = null;
        var secondarySkillMap = hero.getSecondarySkillMap();
        var heroSpecialtyProperty = heroSpecialty.property();
        var heroSpecialtyType = heroSpecialty.specialtyType();
        if (heroSpecialtyType.equals(SECONDARY_SKILL)) {
            secondarySkillSpecialtyLevel = secondarySkillMap
                    .get((SecondarySkill) specialtyProperty);
            if (secondarySkillSpecialtyLevel == null) {
                log.warn("Secondary skill is missing while Specialty has been set");
                log.warn(applyBasicSecondarySkillIfMissing(hero, heroSpecialtyProperty.toString()));
                secondarySkillSpecialtyLevel = SkillLevel.BASIC;
            }
        }
        return (int) switch (heroSpecialtyType) {
            case CREATURE, UPGRADE, WAR_MACHINE -> 0;
            case SPEED -> 2;
            case RESOURCE, SPELL -> 1;
            case SECONDARY_SKILL -> {
                var secSkillArrayIndex = getSecondarySkillModifierNumber(secondarySkillSpecialtyLevel);
                var secondarySkill = SecondarySkill.valueOf(String.valueOf(specialtyProperty));
                var modifier = secondarySkill.getSkillLevelModifiers()[secSkillArrayIndex];
                yield Math.round(modifier * (1 + heroLevel * 0.05));
            }
            //todo amend other heroSpecialtyProperty calculations
            // creature -> Increases Speed of creatures and their Attack and Defense skills for every x levels (rounded up)
            //FIXED_CREATURES_SPECIALTY on desktop
        };
    }

    private Integer getSecondarySkillModifierNumber(SkillLevel skillLevel) {
        return switch (skillLevel) {
            case BASIC -> 0;
            case ADVANCED -> 1;
            case EXPERT -> 2;
            case CUSTOM -> throw new IllegalArgumentException("Not applicable for HeroSpecialty");
        };
    }

    private static void detachArtifact(String artifactName, Hero hero) {
        var artifactSlot = ArtifactSlotDisposition.valueOf(artifactName).getArtifactSlot();
        var entityField = artifactSlot.getEntityField();
        var bodyMap = getOrCreateBodyInvMap(hero);
        switch (entityField) {
            case BODY -> {
                var deleted = bodyMap.remove(artifactSlot);
                if (deleted == null) {
                    throw new IllegalStateException("Artifact " + artifactName + " has not been DETACHED, " +
                            "entity Field of type [" + entityField + "] did not contain it");
                }
            }
            case PROPRIETORY -> throw new IllegalArgumentException("Proprietory NOT implemented");
            case MISC -> {
                var miscInvMap = getOrCreateMiscInvMap(hero);
                for (Map.Entry<ArtifactSlot, ArtifactSlotDisposition> entry : miscInvMap.entrySet()) {
                    var key = entry.getKey();
                    var value = entry.getValue();
                    if (value.name().equals(artifactName)) {
                        miscInvMap.remove(key);
                        log.info(" ::: {} detached from {} slot", artifactName, key);
                        return;
                    }
                }
                throw new IllegalStateException("Artifact " + artifactName + " has not been DETACHED, " +
                        "entity Field of type [" + entityField + "] did not contain it");
            }
            case BACKPACK -> throw new IllegalArgumentException("Backpack NOT implemented");
        }
    }

    private static void attachArtifactToHero(
            String artifactName, Hero hero) {
        var newArtifactSlotDisposition = ArtifactSlotDisposition
                .valueOf(artifactName);
        var newArtifactSlot = newArtifactSlotDisposition.getArtifactSlot();
        var entityField = newArtifactSlot.getEntityField();
        var bodyInvMap = getOrCreateBodyInvMap(hero);
        switch (entityField) {
            case BODY -> {
                var currentArtifactSlotDisposition = bodyInvMap
                        .get(newArtifactSlot);
                if (currentArtifactSlotDisposition != null) {
                    throw new ArtifactAlreadyAttachedException
                            (currentArtifactSlotDisposition + " " +
                                    "ALREADY attached to " + hero.getName());
                }
                bodyInvMap.put(newArtifactSlot, newArtifactSlotDisposition);
            }
            case PROPRIETORY -> throw new IllegalArgumentException("Proprietory NOT implemented");
            case MISC -> {
                var miscInvMap = getOrCreateMiscInvMap(hero);
                if (miscInvMap.size() == MISC_INV_MAP_SIZE)
                    throw new ArtifactFreeSlotMissingException("No free slots for Misc Inv artifact");
                var freeArtifactSlot = detectFreeMiscSlot(miscInvMap);
                miscInvMap.put(freeArtifactSlot, newArtifactSlotDisposition);
            }
            case BACKPACK -> throw new IllegalArgumentException("Backpack NOT implemented");
        }
    }

    private static ArtifactSlot detectFreeMiscSlot(Map<ArtifactSlot,
            ArtifactSlotDisposition> miscInvMap) {
        var set = EnumSet.copyOf(MISC_SLOTS_SET);
        set.removeAll(miscInvMap.keySet());
        if (set.isEmpty())
            throw new ArtifactFreeSlotMissingException("No misc slots match the required pattern");
        else return set.iterator().next();
    }

    static Map<ArtifactSlot, ArtifactSlotDisposition> getOrCreateMiscInvMap(Hero hero) {
        var miscInvMap = hero.getMiscInventoryMap();
        if (miscInvMap == null) {
            miscInvMap = new FixedSizeMap<>(new HashMap<>(), MISC_INV_MAP_SIZE);
            hero.setMiscInventoryMap(miscInvMap);
        }
        return miscInvMap;
    }

    private static Map<ArtifactSlot, ArtifactSlotDisposition> getOrCreateBodyInvMap(Hero hero) {
        var bodyInvMap = hero
                .getBodyInventoryMap();
        if (bodyInvMap == null) {
            bodyInvMap = new EnumMap<>(ArtifactSlot.class);
            hero.setBodyInventoryMap(bodyInvMap);
        }
        return bodyInvMap;
    }

    public static Map<ArtifactSlot, ArtifactSlotDisposition> getOrCreateMiscInventoryMap(Hero hero) {
        var miscInventoryMap = hero.getMiscInventoryMap();
        if (miscInventoryMap == null) {
            miscInventoryMap = new EnumMap<ArtifactSlot, ArtifactSlotDisposition>(ArtifactSlot.class);
            hero.setMiscInventoryMap(miscInventoryMap);
        }
        return miscInventoryMap;
    }

    public void vanquishHero(Hero hero) {
        heroRepository.delete(hero);
    }

    @Transactional
    public String fetchExperience(String heroId, Integer newExperience) {
        var hero = getHero(heroId);
        var experience = hero.getExperience();
        experience = experience == 0 ? 100 : experience;
        var level = hero.getLevel();
        var summedExp = experience + newExperience;
        if ((double) experience / newExperience >= 0.2) {
            hero.setLevel(level + 1);
        }
        hero.setExperience(summedExp);
        return hero.getName() + " has gain " + hero.getLevel() +
                " level with current exp = " + summedExp;
    }

    @Transactional(readOnly = true)
    public HeroRespDto getMostPowerfulHero(Long playerId) {
        var player = playerService.getPlayer(playerId);
        var allHeroesList = player.getHeroList();
        var heroComparisonMap = new HashMap<String, Integer>();
        for (Hero hero : allHeroesList) {
            var primarySkillMap = hero.getPrimarySkillMap();
            var primarySkillCoefficient = primarySkillMap
                    .values()
                    .stream()
                    .reduce(0, Integer::sum);
            var secondarySkillMap = hero.getSecondarySkillMap();
            int secondarySkillCoefficient = secondarySkillMap
                    .values()
                    .stream()
                    .map(Enum::ordinal)
                    .reduce(0, Integer::sum);
            secondarySkillCoefficient *= secondarySkillMap.size();
            var heroArmy = hero.getArmyList();
            var armyCoefficient = heroArmy.size() * getAllSlotsQuantity(heroArmy) * getAllSlotsLevelSum(heroArmy);
            var experience = hero.getExperience();
            experience = experience == 0 ? 100 : experience;
            var level = hero.getLevel();
            level = level == 0 ? 1 : level;
            var levelExpCoefficient = level * experience;
            var totalCoefficient =
                    primarySkillCoefficient +
                            secondarySkillCoefficient +
                            armyCoefficient +
                            levelExpCoefficient;
            heroComparisonMap.put(hero.getCode(), totalCoefficient);
        }
        log.info(heroComparisonMap);
        return heroMapper.toDto(getHero(getMaxParamsHero(heroComparisonMap)));
    }

    private String getMaxParamsHero(HashMap<String, Integer> heroComparisonMap) {
        var maxIndex = heroComparisonMap
                .values()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);
        return heroComparisonMap
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), maxIndex))
                .findFirst()
                .orElseThrow()
                .getKey();
    }

    private int getAllSlotsLevelSum(List<CreatureSlot> heroArmy) {
        return heroArmy
                .stream()
                .map(creatureSlot -> creatureSlot
                        .getType()
                        .getLevel())
                .reduce(0, Integer::sum);
    }

    private int getAllSlotsQuantity(List<CreatureSlot> heroArmy) {
        return heroArmy
                .stream()
                .map(CreatureSlot::getQuantity)
                .reduce(0, Integer::sum);
    }

    @Transactional
    public String setSpecialty(HeroReqSpecialty dto) {
        var hero = getHero(dto.heroCode());
        var specialtyType = dto.specialtyType();
        var specialtyProperty = dto.property();
        if (hero.getHeroSpecialty() != null) {
            applyBasicSecondarySkillIfMissing(hero, dto.property());
            return String.format("%s already has a specialty %s. Assigning secSkill if missing",
                    hero.getName(), hero.getHeroSpecialty().property());
        }
        var heroSpecialty = new HeroSpecialty(specialtyType, specialtyProperty);
        hero.setHeroSpecialty(heroSpecialty);
        var secSkillReply = applyBasicSecondarySkillIfMissing(hero, specialtyProperty);//assigning the same BASIC secSkill if missing
        return String.format("Specialty %s succ set for %s", specialtyProperty, hero.getName()) + System.lineSeparator() + secSkillReply;
    }

    private String applyBasicSecondarySkillIfMissing(Hero hero, String property) {
        var secondarySkillMap = hero.getSecondarySkillMap();
        var secondarySkill = SecondarySkill.valueOf(property);
        if (secondarySkillMap.containsKey(secondarySkill)) {
            return String.format("%s already obtained %s", hero.getName(), property);
        }
        if (secondarySkillMap.size() == 5) {
            return String.format("%s -> No free SecondarySkill slots for %s", property, hero.getName());
        }
        secondarySkillMap.put(secondarySkill, SkillLevel.BASIC);
        return String.format("%s succ added to SecondarySkills for %s", property, hero.getName());
    }

    public WarMachineRespDto syncHeroWarMachine(String heroCode, WarMachine warMachine) {
        var hero = getHero(heroCode);
        var warMachineInventorySet = getOrCreateWarMachinesInvSet(hero);
        if (!warMachineInventorySet.contains(warMachine))
            throw new IllegalArgumentException(hero.getName() + " doesn't possess the " + warMachine);
        //var heroLevel = hero.getLevel();
        var heroPrimarySkillMap = hero.getPrimarySkillMap();
        int attack = heroPrimarySkillMap.get(PrimarySkill.ATTACK),
                defense = heroPrimarySkillMap.get(PrimarySkill.DEFENSE);
        int updatedAttack = 0, updatedDefense = 0, updatedMinDamage = 0, updatedMaxDamage = 0;
        var artilleryDamageChanceModifier = retrieveArtilleryChanceDamageModifier(hero.getSecondarySkillMap());
        var archeryDamageModifier = retrieveArcheryDamagerModifier(hero.getSecondarySkillMap());
        switch (warMachine) {
            case BALLISTA -> {
                var ballistaPropertiesMap = BALLISTA.getCreatureSkillMap();
                updatedAttack = attack + ballistaPropertiesMap.get(ATTACK).value();
                updatedDefense = defense + ballistaPropertiesMap.get(DEFENSE).value();
                updatedMinDamage = ballistaPropertiesMap.get(DAMAGE).value() * (attack + 5) + artilleryDamageChanceModifier;
                updatedMinDamage += updatedMinDamage * archeryDamageModifier / 100;
                updatedMaxDamage = ballistaPropertiesMap.get(DAMAGE).multipliedValue() * (attack + 5) + artilleryDamageChanceModifier;
                updatedMaxDamage += updatedMaxDamage * archeryDamageModifier / 100;
            }
            case AMMO_CART -> {
                var ammoCartPropertiesMap = AMMO_CART.getCreatureSkillMap();
                updatedDefense = defense + ammoCartPropertiesMap.get(DEFENSE).value();
            }
            case CANNON -> {
                var cannonPropertiesMap = CANNON.getCreatureSkillMap();
                updatedAttack = attack + cannonPropertiesMap.get(ATTACK).value();
                updatedDefense = defense + cannonPropertiesMap.get(DEFENSE).value();
                updatedMinDamage = cannonPropertiesMap.get(DAMAGE).value() * (attack + 1) + artilleryDamageChanceModifier;
                updatedMinDamage += updatedMinDamage * archeryDamageModifier / 100;
                updatedMaxDamage = cannonPropertiesMap.get(DAMAGE).multipliedValue() * (attack + 1) + artilleryDamageChanceModifier;
                updatedMaxDamage += updatedMaxDamage * archeryDamageModifier / 100;
            }
            case FIRST_AID_TENT -> {
                var firstAidTentPropertiesMap = FIRST_AID_TENT.getCreatureSkillMap();
                updatedDefense = defense + firstAidTentPropertiesMap.get(DEFENSE).value();
            }
            case CATAPULT -> {
                var catapultPropertiesMap = CATAPULT.getCreatureSkillMap();
                updatedAttack = attack + catapultPropertiesMap.get(ATTACK).value();
                updatedDefense = defense + catapultPropertiesMap.get(DEFENSE).value();
            }
        }
        //ballista dmg calc : own dmg params * (hero's attack + 5)
        //cannon dmg calc : own dmg params * (hero's attack + 1)
        return new WarMachineRespDto(warMachine, updatedAttack, updatedDefense, updatedMinDamage, updatedMaxDamage);
    }

    private int retrieveArcheryDamagerModifier(Map<SecondarySkill, SkillLevel> secondarySkillMap) {
        var archerySkill = secondarySkillMap.get(ARCHERY);
        if (archerySkill == null) return 0;
        var archerySkillModifierIndex = getSecondarySkillModifierNumber(archerySkill);
        return ARCHERY.getSkillLevelModifiers()[archerySkillModifierIndex];
    }

    private int retrieveArtilleryChanceDamageModifier(Map<SecondarySkill, SkillLevel> secondarySkillMap) {
        var artillerySkill = secondarySkillMap.get(ARTILLERY);
        if (artillerySkill == null) return 0;
        var artillerySkillModifierIndex = getSecondarySkillModifierNumber(artillerySkill);
        var modifierCoefficient = ARTILLERY.getSkillLevelModifiers()[artillerySkillModifierIndex];
        return switch (modifierCoefficient) {
            case 50 -> ThreadLocalRandom.current().nextBoolean() ? 2 : 0;
            case 75 -> ThreadLocalRandom.current().nextDouble() < 0.75 ? 2 : 0;
            case 100 -> 2;
            default -> throw new IllegalStateException("ModifierCoefficient unexpected value: " + modifierCoefficient);
        };
    }

    private Set<WarMachine> getOrCreateWarMachinesInvSet(Hero hero) {
        var warMachineInventorySet = hero.getWarMachineInventorySet();
        if (warMachineInventorySet == null) {
            var set = EnumSet.noneOf(WarMachine.class);
            hero.setWarMachineInventorySet(set);
            return set;
        }
        return warMachineInventorySet;
    }

    @Transactional
    public WarMachineRespDto attachWarmachine(String heroCode, WarMachine warmachine) {
        var hero = getHero(heroCode);
        var warMachineInventorySet = getOrCreateWarMachinesInvSet(hero);
        if (warMachineInventorySet.contains(warmachine))
            throw new IllegalArgumentException(hero.getName() + " already possess the " + warmachine);
        else if (warmachine == WarMachine.BALLISTA && warMachineInventorySet.contains(WarMachine.CANNON) ||
                warmachine == WarMachine.CANNON && warMachineInventorySet.contains(WarMachine.BALLISTA)) {
            var existingWarmachine = retrieveExistingWarmachineToDetach(warmachine);
            log.warn("{} already possess {}, swapping for {}",
                    hero.getName(), existingWarmachine, warmachine);
            detachWarmachine(heroCode, existingWarmachine);
        }
        warMachineInventorySet.add(warmachine);
        return syncHeroWarMachine(heroCode, warmachine);
    }

    private WarMachine retrieveExistingWarmachineToDetach(WarMachine requestedWarMachine) {
        return requestedWarMachine == WarMachine.BALLISTA ? WarMachine.CANNON : WarMachine.BALLISTA;
    }

    @Transactional
    public void provideAllHeroesWithCatapult(Long playerId) {
        var player = playerService.getPlayer(playerId);
        var heroList = player.getHeroList();
        heroList.forEach(hero -> {
            var warMachinesInvSet = getOrCreateWarMachinesInvSet(hero);
            if (!warMachinesInvSet.contains(WarMachine.CATAPULT)) {
                warMachinesInvSet.add(WarMachine.CATAPULT);
                log.info(" --> {} was succ provided with Catapult", hero.getName());
            } else log.error("{} already obtained a Catapult", hero.getName());
        });
    }

    @Transactional
    public void detachWarmachine(String heroCode, WarMachine warmachine) {
        var hero = getHero(heroCode);
        var warMachineInventorySet = hero.getWarMachineInventorySet();
        if (warMachineInventorySet == null) {
            throw new IllegalArgumentException(hero.getName() + " has no " + warmachine + ", as warMachineSet in NULL");
        }
        warMachineInventorySet.remove(warmachine);
        log.info(" --> {} has been succ detached from {}", warmachine, hero.getName());
    }

}