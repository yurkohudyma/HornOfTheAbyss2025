package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
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
            @RequestParam String code){
        return ResponseEntity.ok(heroService
                .fetchHero(code));
    }
    @PostMapping("/reinforce")
    public ResponseEntity<String> addArmyCreatures (@RequestBody ReinforceReqDto dto){
        return ResponseEntity.ok(heroService.reinforceArmy(dto));
    }
}
