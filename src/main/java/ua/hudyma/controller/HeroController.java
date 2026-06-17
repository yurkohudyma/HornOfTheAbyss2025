package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.converter.CreatureTypeRegistry;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroReqSpecialty;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.dto.MovemementPointsRespDto;
import ua.hudyma.domain.heroes.enums.HeroFaction;
import ua.hudyma.dto.WarMachineRespDto;
import ua.hudyma.service.HeroService;
import ua.hudyma.service.RandomService;
import ua.hudyma.util.IdGenerator;

@RestController
@RequestMapping("/heroes")
@RequiredArgsConstructor
public class HeroController {

    private final HeroService heroService;

    private final RandomService randomService;

    @GetMapping("/getMovePoints")
    public ResponseEntity<MovemementPointsRespDto>
    updateAndFetchHeroMovementPoints
            (@RequestParam String heroCode) {
        return ResponseEntity.ok(heroService
                .updateAndFetchHeroMovementPoints(heroCode));
    }

    @GetMapping("/syncHeroWarMachineOnSpecialty")
    public ResponseEntity<WarMachineRespDto>
    syncHeroWarMachineOnSpecialty
            (@RequestParam String heroCode) {
        return ResponseEntity.ok(heroService
                .syncHeroWarMachine(heroCode));
    }

    public String getRandomCreature(HeroFaction heroFaction) {
        var faction = heroFaction.getFaction();
        var allFactionCreatures = CreatureTypeRegistry
                .getAllCreaturesByFaction(faction, true);
        return allFactionCreatures[IdGenerator
                .getThreadLocalRandomIndex(
                        0, allFactionCreatures.length)].getCode();
    }

    @GetMapping("/getRandom")
    public ResponseEntity<HeroRespDto> createRandomHero() {
        return ResponseEntity.ok(randomService
                .createRandomHeroDto());
    }

    @PatchMapping("/setSpecialty")
    public ResponseEntity<String> setSpecialty(
            @RequestBody HeroReqSpecialty dto) {
        return ResponseEntity.ok(heroService.setSpecialty(dto));
    }

    @GetMapping("/calcSpecialtyModifier")
    public ResponseEntity<Integer> calcSpecialtyModifier(
            @RequestParam String heroCode) {
        return ResponseEntity.ok(heroService
                .calcSpecialtyModifier(heroCode));
    }

    @GetMapping("/getBest")
    public ResponseEntity<HeroRespDto> getMostPowerfulHero(
            @RequestParam Long playerId) {
        return ResponseEntity.ok(heroService
                .getMostPowerfulHero(playerId));
    }

    @GetMapping("/gainExperience")
    public ResponseEntity<String> gainExperience(
            @RequestParam String heroId,
            @RequestParam Integer experience) {
        return ResponseEntity.ok(heroService
                .gainExperience(heroId, experience));
    }
    @PostMapping
    public ResponseEntity<String> createHero(
            @RequestBody HeroReqDto dto) {
        return ResponseEntity.ok(heroService
                .createHero(dto));
    }
    @GetMapping
    public ResponseEntity<HeroRespDto> fetchHero(
            @RequestParam String heroCode) {
        return ResponseEntity.ok(heroService
                .fetchHero(heroCode));
    }
    @PatchMapping("/attachArtifact")
    public ResponseEntity<HeroRespDto>
    syncHeroSkillsUponArtifactAttachment(
            @RequestParam String heroId,
            @RequestParam String artifact) {
        return ResponseEntity.ok(heroService
                .syncHeroSkillsUponArtifactAttachment
                        (heroId, artifact));
    }

    @PatchMapping("/detachArtifact")
    public ResponseEntity<HeroRespDto>
    syncHeroSkillsUponArtifactDetachment(
            @RequestParam String heroId,
            @RequestParam String artifact) {
        return ResponseEntity.ok(heroService
                .syncHeroSkillsUponArtifactDetachment
                        (heroId, artifact));
    }
    @PatchMapping("/defaultPrimarySkills")
    public ResponseEntity<HeroRespDto> defaultPrimarySkills(
            @RequestParam String heroId) {
        return ResponseEntity.ok(heroService
                .defPriSkillsEmptyBodyInvMapAndParamMap
                        (heroId));
    }

}
