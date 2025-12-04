package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.dto.CreatureReqDto;
import ua.hudyma.domain.creatures.dto.CreatureRespDto;
import ua.hudyma.service.CreatureService;

@RestController
@RequestMapping("/creatures")
@RequiredArgsConstructor
public class CreatureController {
    private final CreatureService creatureService;

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
}
