package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.Creature;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureReqDto;
import ua.hudyma.domain.creatures.dto.CreatureRespDto;
import ua.hudyma.domain.creatures.dto.CreatureSkillValue;
import ua.hudyma.service.CreatureService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/creatures")
@RequiredArgsConstructor
public class CreatureController {
    private final CreatureService creatureService;

    @GetMapping("/detectNullModifierValueCreaturesAndEqualise")
    public ResponseEntity<String> detectNullMultipliedValuesAndEqualiseIfNeeded(){
        // all players, all heroes
        return ResponseEntity.ok(creatureService
                .equiliseNullMultipliedValues());
    }

    @PostMapping
    public ResponseEntity<String> createCreature (
            @RequestBody CreatureReqDto dto){
        return ResponseEntity.ok(creatureService
                .createCreature(dto));
    }

    @GetMapping
    public ResponseEntity<CreatureRespDto> fetchCreature (
            @RequestParam Long id){
        return ResponseEntity.ok(creatureService
                .fetchCreature(id));
    }

    @GetMapping("/by")
    public ResponseEntity<Optional<Creature>> fetchCreatureByType
            (@RequestParam CreatureType type){
        return ResponseEntity.ok(creatureService
                .fetchCreatureByType(type));
    }

    @GetMapping("/getSlowestCreatureSpeed")
    public ResponseEntity<Integer> getHeroSlowestCreatureSpeedValue(
            @RequestParam String heroId) {
        return ResponseEntity.ok(creatureService
                .getHeroSlowestCreatureSpeedValue(heroId));
    }


}
