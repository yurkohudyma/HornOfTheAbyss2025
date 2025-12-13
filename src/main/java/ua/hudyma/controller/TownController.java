package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.service.TownService;

@RestController
@RequestMapping("/towns")
@RequiredArgsConstructor
public class TownController {
    private final TownService townService;
    @PostMapping
    public ResponseEntity<String> createTown (@RequestBody TownReqDto dto){
        return ResponseEntity.ok(townService.createTown(dto));
    }
    @GetMapping
    public ResponseEntity<TownRespDto> fetchTown (@RequestParam String name){
        return ResponseEntity.ok(townService.fetchTown(name));
    }
    @GetMapping("/allocateVisitor")
    public ResponseEntity<String> allocateHeroAsVisitorInTown (
            @RequestParam String heroId, @RequestParam String townName){
        return ResponseEntity.ok(townService.allocateVisitingHero(heroId, townName));
    }

    @GetMapping("/swapHeroes")
    public ResponseEntity<String> allocateHeroAsVisitorInTown (
            @RequestParam String townName){
        return ResponseEntity.ok(townService.swapHeroesInTown(townName));
    }
}
