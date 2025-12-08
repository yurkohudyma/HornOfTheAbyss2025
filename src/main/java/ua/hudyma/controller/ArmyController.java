package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.dto.SplitReqDto;
import ua.hudyma.domain.heroes.dto.CreatureSlotRespDto;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.service.ArmyService;

import java.util.List;

@RestController
@RequestMapping("/army")
@RequiredArgsConstructor
public class ArmyController {
    private final ArmyService armyService;

    @PostMapping("/reinforce")
    public ResponseEntity<String> reinforceArmy (
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

    @GetMapping("/full")
    public ResponseEntity<List<CreatureSlot>> viewArmy (
            @RequestParam String heroId){
        return ResponseEntity.ok(armyService
                .viewArmy(heroId));
    }

    @GetMapping
    public ResponseEntity<List<CreatureSlotRespDto>>
    viewArmyShort (
            @RequestParam String heroId){
        return ResponseEntity.ok(armyService
                .viewArmyShort(heroId));
    }

    @GetMapping("/compress")
    public ResponseEntity<String> compressArmy (
            @RequestParam String heroId){
        return ResponseEntity.ok(armyService
                .compressArmy(heroId));
    }
    @PatchMapping("/split")
    public ResponseEntity<String> splitArmy (
            @RequestBody SplitReqDto dto){
        return ResponseEntity.ok(armyService
                .splitSlot(dto));
    }

    @PatchMapping("/distribute")
    public ResponseEntity<String> distributeSlot (
            @RequestBody SplitReqDto dto){
        return ResponseEntity.ok(armyService
                .splitAndDistribute(dto));
    }
    @PatchMapping("/exchange")
    public ResponseEntity<String> exchangeArmies (
            @RequestParam String hero1Id,
            @RequestParam String hero2Id){
        return ResponseEntity.ok(armyService
                .exchangeArmies(hero1Id, hero2Id));
    }
    @PatchMapping("/transfer")
    public ResponseEntity<String> transferArmy (
            @RequestParam String hero1Id,
            @RequestParam String hero2Id){
        return ResponseEntity.ok(armyService
                .transferArmy(hero1Id, hero2Id));
    }
}
