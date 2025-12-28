package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.artifacts.enums.ArtifactAction;
import ua.hudyma.domain.artifacts.enums.ArtifactProperties;
import ua.hudyma.domain.artifacts.enums.ArtifactSlotDisposition;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.enums.ArtifactSlot;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.exception.ArtifactAlreadyAttachedException;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.repository.HeroRepository;

import java.util.*;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;
import static ua.hudyma.util.MessageProcessor.getReturnMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class HeroService {
    private final HeroMapper heroMapper;
    private final HeroRepository heroRepository;
    private final PlayerService playerService;
    //private final ArmyService armyService; invokes circular dependency exception

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
    public HeroRespDto defaultPrimarySkillsAndEmptyBodyInvMap(String heroId) {
        var hero = getHero(heroId);
        var skillMap = hero.getPrimarySkillMap();
        for (Map.Entry<PrimarySkill, Integer> entry : skillMap.entrySet()) {
            entry.setValue(0);
        }
        hero.setBodyInventoryMap(Map.of());
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
        resolveNotApplicableArtifactAction(artifactAction); //temporary solution for missing bizlog
        var primarySkillsMap = hero.getPrimarySkillMap();
        var actionData = artifact.getActionData();
        for (Map.Entry<String, Object> entry : actionData.entrySet()) {
            var skillName = entry.getKey();
            if (skillName != null && !skillName.isEmpty()) {
                var primarySkillEnum = PrimarySkill.valueOf(skillName);
                var boostableValue = entry.getValue();
                primarySkillsMap.computeIfPresent(primarySkillEnum,
                        (k, v) -> v + (Integer) boostableValue);
            }
        }
        attachArtifactToHero(artifactName, hero);
        //armyService.syncArmySkillsWithHero(hero.getArmyList(), hero); <-- deactivated for testing
        return heroMapper.toDto(hero);
    }

    @Transactional
    public HeroRespDto syncHeroSkillsUponArtifactDetachment(String heroId, String artifactName) {
        var hero = getHero(heroId);
        var artifactPropMap = ArtifactProperties.valueOf(artifactName).getActionData();
        var heroSkillMap = hero.getPrimarySkillMap();
        for (Map.Entry<String, Object> entry : artifactPropMap.entrySet()) {
            var primarySkillEnum = PrimarySkill.valueOf(entry.getKey());
            heroSkillMap.computeIfPresent(primarySkillEnum, (k, v) -> {
                if (v <= 0) return v + Math.abs((Integer) entry.getValue());
                else return (Integer) entry.getValue() - v;
            });
        }
        detachArtifact(artifactName, hero);
        //armyService.syncArmySkillsWithHero(hero.getArmyList(), hero); <-- deactivated for testing
        return heroMapper.toDto(hero);
    }

    private static void detachArtifact(String artifactName, Hero hero) {
        var artifactSlot = ArtifactSlotDisposition.valueOf(artifactName).getArtifactSlot();
        var entityField = artifactSlot.getEntityField();
        switch (entityField) {
            case BODY -> {
                var bodyMap = hero.getBodyInventoryMap();
                var deleted = bodyMap.remove(artifactSlot);
                if (deleted == null) {
                    throw new IllegalStateException("Artifact " + artifactName + " has not been DETACHED, " +
                            "entity Field of type = " + entityField + " did not contain it");
                }
            }
            case PROPRIETORY -> {
            }
            case MISC -> {
            }
        }
    }

    private static void attachArtifactToHero(
            String artifactName, Hero hero) {
        var newArtifactSlotDisposition = ArtifactSlotDisposition
                .valueOf(artifactName);
        var newArtifactSlot = newArtifactSlotDisposition.getArtifactSlot();
        var entityField = newArtifactSlot.getEntityField();
        switch (entityField) {
            case BODY -> {
                var bodyInvMap = hero
                        .getBodyInventoryMap();
                if (bodyInvMap == null) {
                    bodyInvMap = new EnumMap<>(ArtifactSlot.class);
                    hero.setBodyInventoryMap(bodyInvMap);
                } else {
                    var currentArtifactSlotDisposition = bodyInvMap
                            .get(newArtifactSlot);
                    if (currentArtifactSlotDisposition != null) {
                        throw new ArtifactAlreadyAttachedException
                                (currentArtifactSlotDisposition + " " +
                                        "ALREADY attached to " + hero.getName());
                    }
                }
                bodyInvMap.put(newArtifactSlot, newArtifactSlotDisposition);
            }
            case PROPRIETORY -> {
            }
            case MISC -> {
            }
        }
    }

    private static void resolveNotApplicableArtifactAction(
            ArtifactAction artifactAction) {
        switch (artifactAction) {
            case COMPLEX, ENEMY_BOOST, MODIFIER -> throw new IllegalArgumentException
                    ("COMPLEX/ENEMY_BOOST/MODIFIER action in ARTIFACTS not supported");
            case BOOST -> {
            }
        }
    }

    public void vanquishHero(Hero hero) {
        heroRepository.delete(hero);
    }
}