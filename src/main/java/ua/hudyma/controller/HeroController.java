package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.service.HeroService;

@RestController
@RequestMapping("/heroes")
@RequiredArgsConstructor
public class HeroController {
    private final HeroService heroService;
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
