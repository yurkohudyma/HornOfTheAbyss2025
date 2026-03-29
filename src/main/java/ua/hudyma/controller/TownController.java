package ua.hudyma.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.domain.towns.enums.FortificationType;
import ua.hudyma.dto.TownGenerCreaturesReport;
import ua.hudyma.dto.TownHireCreaturesReqDto;
import ua.hudyma.resource.ResourceDemandRespDto;
import ua.hudyma.service.TownService;
import ua.hudyma.service.build.AbstractBuildService;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/towns")
@RequiredArgsConstructor
public class TownController {
    private final TownService townService;
    private final AbstractBuildService abstractBuildService;

    @PatchMapping("/replaceSpell")
    public ResponseEntity<String> studyAnotherSpell (
            @RequestParam String townName,
            @RequestParam String existingSpellName){
        return ResponseEntity.ok(townService
                .replaceTownSpell(townName,
                        existingSpellName));
    }

    @GetMapping("/getTownStatByFortification")
    public ResponseEntity<Map<FortificationType, Integer>>
            getTownStatByFortification (Long playerId){
        return ResponseEntity.ok(townService
                .getTownFortificationStatsCYCLE(playerId));
    }

    @GetMapping("/getAllTownCreatureTypes")
    public ResponseEntity<CreatureType[]> getAllTownBasicCreatures (
            @RequestParam String townName, @RequestParam boolean essential){
        return ResponseEntity.ok(townService
                .getAllCreaturesTypes(townName, essential));
    }

    @PostMapping("/hireCreatures")
    public ResponseEntity<List<CreatureSlot>> hireCreatures
            (@RequestBody TownHireCreaturesReqDto dto) {
        return ResponseEntity.ok(townService.hireCreatures(dto));
    }

    @GetMapping("/getTownCreaturesForHire")
    public ResponseEntity<TownGenerCreaturesReport> getTownGenCreaturesForHire
            (@RequestParam String townName){
        return ResponseEntity.ok(townService
                .getAvailCreaturesForHire(townName));
    }

    @GetMapping("/generateWeeklyCreatures")
    public ResponseEntity<List<TownGenerCreaturesReport>>
    generateAllTownsWeeklyCreatures (@RequestParam Long playerId){
        return ResponseEntity.ok(townService
                .generateWeeklyCreatures(playerId));
    }

    @PostMapping
    public ResponseEntity<String> createTown (
            @RequestBody TownReqDto dto){
        return ResponseEntity.ok(townService.createTown(dto));
    }
    @GetMapping
    public ResponseEntity<TownRespDto> fetchTown (
            @RequestParam String name){
        return ResponseEntity.ok(townService.fetchTown(name));
    }
    @GetMapping("/allocateVisitor")
    public ResponseEntity<String> allocateHeroAsVisitorInTown (
            @RequestParam String heroId,
            @RequestParam String townName){
        return ResponseEntity.ok(townService
                .allocateVisitingHero(heroId, townName));
    }

    @GetMapping("/swapHeroes")
    public ResponseEntity<String> allocateHeroAsVisitorInTown (
            @RequestParam String townName){
        return ResponseEntity.ok(townService
                .swapHeroesInTown(townName));
    }


    @GetMapping("/getResourceDemand")
    public ResponseEntity<ResourceDemandRespDto> getResourceDemand
            (@RequestParam String type, @RequestParam Integer level){
        return ResponseEntity.ok(abstractBuildService
                .getResourceDemand(type, level));
    }
}
