package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.artifacts.enums.ArtifactProperties;
import ua.hudyma.domain.artifacts.enums.ArtifactSlotDisposition;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.HeroParams;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.enums.ArtifactSlot;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.exception.ArtifactAlreadyAttachedException;
import ua.hudyma.exception.ArtifactFreeSlotMissingException;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.repository.HeroRepository;
import ua.hudyma.util.FixedSizeMap;

import java.util.*;

import static ua.hudyma.domain.heroes.HeroParams.CUR_SPELL_POINTS;
import static ua.hudyma.domain.heroes.HeroParams.MAX_SPELL_POINTS;
import static ua.hudyma.domain.heroes.enums.ArtifactSlot.*;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.KNOWLEDGE;
import static ua.hudyma.domain.heroes.enums.SecondarySkill.INTELLIGENCE;
import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;
import static ua.hudyma.util.MessageProcessor.getReturnMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class HeroService {
    private final static int MISC_INV_MAP_SIZE = 5;
    private final static Set<ArtifactSlot> MISC_SLOTS_SET = Set.of(MISC_A, MISC_B, MISC_C, MISC_D, MISC_E);
    private final HeroMapper heroMapper;
    private final HeroRepository heroRepository;
    private final PlayerService playerService;
    private final ArmyHeroService armyHeroService;

    @SneakyThrows
    @Transactional
    public String createHero(HeroReqDto dto) {
        var hero = heroMapper.toEntity(dto);
        var player = playerService.getPlayer(dto.playerId());
        hero.setPlayer(player);
        heroRepository.save(hero);
        return getReturnMessage(hero, "name");
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
                        EntityNotFoundException::new, false));
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
        }

        attachArtifactToHero(artifactName, hero);
        armyHeroService.syncArmySkillsWithHero(hero.getArmyList(), hero);
        return heroMapper.toDto(hero);
    }

    private static Map<HeroParams, Integer> getOrCreateParamMap(Hero hero) {
        return hero.getParametersMap() == null ?
                new FixedSizeMap<>(new HashMap<>(), 4) :
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

    public static void syncSpellPointsValues(Hero hero) {
        var paramMap = hero.getParametersMap();
        if (paramMap == null) {
            paramMap = new FixedSizeMap<>(new HashMap<>(), 4);
        }
        var intelligenceLevel = getIntelligenceLevel(hero);
        var knowledgeLevel = getKnowledgeLevel(hero);
        paramMap.put(MAX_SPELL_POINTS, (int) (knowledgeLevel * 10 * intelligenceLevel));
        /*if (!paramMap.containsKey(CUR_SPELL_POINTS)) {
            paramMap.put(CUR_SPELL_POINTS, paramMap.get(MAX_SPELL_POINTS));
        }*/
        paramMap.putIfAbsent(CUR_SPELL_POINTS, paramMap.get(MAX_SPELL_POINTS));
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
                    }
                    log.info(" ::: " + artifactName + " detached from " + key + " slot");
                    return;
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

    private static ArtifactSlot detectFreeMiscSlot(Map<ArtifactSlot, ArtifactSlotDisposition> miscInvMap) {
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

    public void vanquishHero(Hero hero) {
        heroRepository.delete(hero);
    }
}