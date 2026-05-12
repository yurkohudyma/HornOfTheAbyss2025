package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.converter.CreatureTypeRegistry;
import ua.hudyma.domain.heroes.dto.CalcSpecialtyReq;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroReqSpecialty;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.enums.HeroFaction;
import ua.hudyma.service.HeroService;
import ua.hudyma.service.RandomService;
import ua.hudyma.util.IdGenerator;

@RestController
@RequestMapping("/heroes")
@RequiredArgsConstructor
public class HeroController {
    private final HeroService heroService;
    private final RandomService randomService;

    public String getRandomCreature(HeroFaction heroFaction) {
        var faction = heroFaction.getFaction();
        var allFactionCreatures = CreatureTypeRegistry.getAllCreaturesByFaction(faction, true);
        return allFactionCreatures[IdGenerator.getThreadLocalRandomIndex(0, allFactionCreatures.length)].getCode();
    }

    @GetMapping("/getRandom")
    public ResponseEntity<HeroRespDto> createRandomHero (){
        return ResponseEntity.ok(randomService
                .createRandomHeroDto());
    }

    @PatchMapping("/setSpecialty")
    public ResponseEntity<HeroRespDto> setSpecialty(
            @RequestBody HeroReqSpecialty dto){
        return ResponseEntity.ok(heroService.setSpecialty(dto));
    }

    @PostMapping("/calcSpecialtyResult")
    public ResponseEntity<Integer> calcSpecialtyResult(
            @RequestBody CalcSpecialtyReq dto){
        return ResponseEntity.ok(heroService.calcSpecialtyResult(dto));
    }

    @GetMapping("/getBest")
    public ResponseEntity<HeroRespDto> getMostPowerfulHero (
            @RequestParam Long playerId){
        return ResponseEntity.ok(heroService
                .getMostPowerfullHero(playerId));
    }

    @GetMapping("/gainExperience")
    public ResponseEntity<String> gainExperience (
            @RequestParam String heroId,
            @RequestParam Integer experience){
        return ResponseEntity.ok(heroService
                .gainExperience(heroId, experience));
    }
    @PostMapping
    public ResponseEntity<String> createHero (
            @RequestBody HeroReqDto dto){
        return ResponseEntity.ok(heroService
                .createHero(dto));
    }
    @GetMapping
    public ResponseEntity<HeroRespDto> fetchHero (
            @RequestParam String heroCode){
        return ResponseEntity.ok(heroService
                .fetchHero(heroCode));
    }
    @PatchMapping("/attachArtifact")
    public ResponseEntity<HeroRespDto>
    syncHeroSkillsUponArtifactAttachment(
            @RequestParam String heroId,
            @RequestParam String artifact){
        return ResponseEntity.ok(heroService
                .syncHeroSkillsUponArtifactAttachment
                        (heroId, artifact));
    }

    @PatchMapping("/detachArtifact")
    public ResponseEntity<HeroRespDto>
    syncHeroSkillsUponArtifactDetachment(
            @RequestParam String heroId,
            @RequestParam String artifact){
        return ResponseEntity.ok(heroService
                .syncHeroSkillsUponArtifactDetachment
                        (heroId, artifact));
    }
    @PatchMapping("/defaultPrimarySkills")
    public ResponseEntity<HeroRespDto> defaultPrimarySkills (
            @RequestParam String heroId){
        return ResponseEntity.ok(heroService
                .defPriSkillsEmptyBodyInvMapAndParamMap
                        (heroId));
    }
}
