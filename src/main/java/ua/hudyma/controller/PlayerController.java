package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.players.dto.PlayerReqDto;
import ua.hudyma.domain.players.dto.PlayerRespDto;
import ua.hudyma.domain.players.dto.ResourcesReqDto;
import ua.hudyma.resource.enums.MineType;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.service.PlayerService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/mines")
    public ResponseEntity<Map<MineType, Integer>> getMines (
            @RequestParam Long playerId){
        return ResponseEntity.ok(playerService.getMines(playerId));
    }

    @GetMapping("/addMine")
    public ResponseEntity<String> addMine(
            @RequestParam MineType mineType, @RequestParam Long playerId){
        return ResponseEntity.ok(playerService.addMine(mineType, playerId));
    }

    @GetMapping("/minesWeeklyIncome")
    public ResponseEntity<Map<ResourceType, Integer>> getMinesWeeklyReport
            (@RequestParam Long playerId){
        return ResponseEntity.ok(playerService.getMinesWeeklyIncome(playerId));
    }

    @PostMapping
    public ResponseEntity<String> createPlayer (
            @RequestBody PlayerReqDto dto){
        return ResponseEntity.ok(playerService
                .createPlayer(dto));
    }
    @GetMapping("/resources")
    public ResponseEntity<Map<ResourceType, Integer>>
    fetchResources (
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
    public ResponseEntity<Integer> calcDailyIncome (
            @RequestParam Long playerId){
        return ResponseEntity.ok(playerService
                .calcDailyIncome(playerId));
    }

    @GetMapping
    public ResponseEntity<PlayerRespDto> fetchPlayer(
            @RequestParam Long playerId){
        return ResponseEntity.ok(playerService
                .fetchPlayer(playerId));
    }
}
