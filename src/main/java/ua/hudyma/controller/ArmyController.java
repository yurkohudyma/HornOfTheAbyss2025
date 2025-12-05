package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.service.ArmyService;
import ua.hudyma.service.CreatureService;
import ua.hudyma.service.HeroService;

import java.util.List;

@RestController
@RequestMapping("/army")
@RequiredArgsConstructor
public class ArmyController {
    private final HeroService heroService;
    private final CreatureService creatureService;
    private final ArmyService armyService;

    @PostMapping("/reinforce")
    public ResponseEntity<String> addArmyCreatures (
            @RequestBody ReinforceReqDto dto){
        return ResponseEntity.ok(armyService
                .reinforceArmy(dto));
    }

    @PatchMapping
    public ResponseEntity<String> deleteArmySlot (
            @RequestParam String slotId,
            @RequestParam String heroId){
        return ResponseEntity.ok(armyService
                .deleteArmySlot(slotId, heroId));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteArmy (
            @RequestParam String heroId){
        return ResponseEntity.ok(armyService
                .deleteArmy(heroId));
    }

    @GetMapping
    public ResponseEntity<List<CreatureSlot>> viewArmy (
            @RequestParam String heroId){
        return ResponseEntity.ok(armyService.viewArmy(heroId));
    }

    @GetMapping("/compress")
    public ResponseEntity<String> compressArmy (
            @RequestParam String heroId){
        return ResponseEntity.ok(armyService.compressArmy(heroId));
    }
}
