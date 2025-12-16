package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.players.dto.PlayerReqDto;
import ua.hudyma.domain.players.dto.PlayerRespDto;
import ua.hudyma.domain.players.dto.ResourcesReqDto;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.service.PlayerService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;
    @PostMapping
    public ResponseEntity<String> createPlayer (@RequestBody PlayerReqDto dto){
        return ResponseEntity.ok(playerService.createPlayer(dto));
    }
    @GetMapping("/resources")
    public ResponseEntity<Map<ResourceType, Integer>> fetchResources (
            @RequestParam Long playerId){
        return ResponseEntity.ok(playerService
                .fetchResource(playerId));
    }

    @PostMapping("/addResources")
    public ResponseEntity<String> addResources (@RequestBody
                                ResourcesReqDto dto){
        return ResponseEntity.ok(playerService
                .addResources(dto));
    }

    @GetMapping("/calcIncome")
    public ResponseEntity<Integer> calcDailyIncome (@RequestParam Long playerId){
        return ResponseEntity.ok(playerService.calcDailyIncome(playerId));
    }
}
