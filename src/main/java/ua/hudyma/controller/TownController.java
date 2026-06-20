package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.resource.enums.ResourceType;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.domain.towns.enums.FortificationType;
import ua.hudyma.dto.TownGenerCreaturesReport;
import ua.hudyma.dto.TownHireCreaturesReqDto;
import ua.hudyma.domain.resource.ResourceDemandRespDto;
import ua.hudyma.service.TownService;
import ua.hudyma.service.build.AbstractBuildService;
import ua.hudyma.service.build.GrailBuildingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/towns")
@RequiredArgsConstructor
public class TownController {
    private final TownService townService;
    private final AbstractBuildService abstractBuildService;
    private final GrailBuildingService grailBuildingService;

    @GetMapping("/createRandom")
    public ResponseEntity<List<TownRespDto>>
    getRandomTowns(@RequestParam int qty){
        return ResponseEntity.ok(townService
                .createRandomTowns(qty));
    }

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
    public ResponseEntity<CreatureType[]> getAllTownEssentialCreatures(
            @RequestParam String townName, @RequestParam boolean essential){
        return ResponseEntity.ok(townService
                .getAllTownCreaturesTypes(townName, essential));
    }

    @PostMapping("/hireCreatures")
    public ResponseEntity<List<CreatureSlot>> hireCreatures
            (@RequestBody TownHireCreaturesReqDto dto) {
        return ResponseEntity.ok(townService.hireCreatures(dto));
    }

    @GetMapping("/calcAllHireableCreaturesCost")
    public ResponseEntity<Map<ResourceType, Integer>> calcAllHireableCreatures
            (@RequestParam String townName){
        return ResponseEntity.ok(townService
                .calcAllHireableCreatures(townName));
    }

    @GetMapping("/getTownCreaturesForHire")
    public ResponseEntity<TownGenerCreaturesReport> getAvailCreaturesForHire
            (@RequestParam String townName){
        return ResponseEntity.ok(townService
                .getAvailCreaturesForHire(townName));
    }

    @GetMapping("/generateWeeklyCreaturesAllTowns")
    public ResponseEntity<List<TownGenerCreaturesReport>>
    generateAllTownsWeeklyCreatures (@RequestParam Long playerId){
        return ResponseEntity.ok(townService
                .generateAllTownsWeeklyCreatures(playerId));
    }

    @GetMapping("/generateWeeklyCreatures")
    public ResponseEntity<TownGenerCreaturesReport>
    generateWeeklyCreatures (@RequestParam String townName) {
        return ResponseEntity.ok(townService
                .generateWeeklyCreatures(townName));
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

    @GetMapping("/calculateGrailSpellDamage") // i.e. LIGHTNING_ROD
    public ResponseEntity<Integer> calculateGrailSpellDamage
            (@RequestParam String townName){
        return ResponseEntity.ok(grailBuildingService
                .calculateGrailSpellDamage(townName));
    }
}
