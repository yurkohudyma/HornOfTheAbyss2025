package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.enums.AttackResultDto;
import ua.hudyma.service.BattlefieldService;
import ua.hudyma.service.CombatService;

@RestController
@RequestMapping("/combat")
@RequiredArgsConstructor
public class CombatController {
    private final CombatService combatService;
    private final BattlefieldService battlefieldService;

    @GetMapping
    public ResponseEntity<AttackResultDto> init (
            @RequestParam String attackerId, @RequestParam String defenderId){
        return ResponseEntity.ok(combatService.initBattle(attackerId, defenderId));
    }
    @GetMapping("/initBattlefield")
    public ResponseEntity<String> initBattlefield (){
        return ResponseEntity.ok(combatService.initBattlefield());
    }

    @GetMapping("/renderBattlefield")
    public void reinitBattlefield (){
        battlefieldService.renderBattlefield();
    }
}
