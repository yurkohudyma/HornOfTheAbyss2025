package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.dto.BattleResultDto;
import ua.hudyma.dto.SpellAttackResultDto;
import ua.hudyma.dto.SpellCastCombatReqDto;
import ua.hudyma.service.BattlefieldService;
import ua.hudyma.service.CombatService;

import java.util.Map;

@RestController
@RequestMapping("/combat")
@RequiredArgsConstructor
public class CombatController {
    private final CombatService combatService;
    private final BattlefieldService battlefieldService;

    @PostMapping("/calcExperienceAfterCombat")
    public ResponseEntity<Integer> calcExperienceAfterCombat (
            @RequestBody Map<CreatureType, Integer> creatureMap){
        return ResponseEntity.ok(combatService
                .calcExperienceAfterCombat(creatureMap));
    }

    @GetMapping
    public ResponseEntity<BattleResultDto> attack (
            @RequestParam String attackerId,
            @RequestParam String defenderId){
        return ResponseEntity.ok(combatService
                .engageBattle(attackerId, defenderId));
    }
    @GetMapping("/initBattlefield")
    public ResponseEntity<String> initBattlefield (){
        return ResponseEntity.ok(combatService
                .initBattlefield());
    }

    @GetMapping("/renderBattlefield")
    public void reinitBattlefield (){
        battlefieldService.renderBattlefield();
    }

    @PostMapping("/castSpell")
    public ResponseEntity<SpellAttackResultDto> spellCast (
            @RequestBody SpellCastCombatReqDto dto) {
        return ResponseEntity.ok(combatService
                .spellCast(dto));
    }


}
