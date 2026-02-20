package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.dto.TownGenerCreaturesReport;
import ua.hudyma.resource.ResourceDemandRespDto;
import ua.hudyma.service.TownService;
import ua.hudyma.service.build.AbstractBuildService;

import java.util.List;

@RestController
@RequestMapping("/towns")
@RequiredArgsConstructor
public class TownController {
    private final TownService townService;
    private final AbstractBuildService abstractBuildService;

    @GetMapping("/getTownCreaturesForHire")
    public ResponseEntity<TownGenerCreaturesReport> getTownGenCreaturesForHire
            (@RequestParam String townName){
        return ResponseEntity.ok(townService.getAvailCreaturesForHire(townName));
    }

    @GetMapping("generateWeeklyCreatures")
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
