package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.dto.SplitReqDto;
import ua.hudyma.domain.heroes.dto.CreatureSlotRespDto;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.domain.map.dto.MapCoordinatesDto;
import ua.hudyma.domain.map.dto.TownDataDto;
import ua.hudyma.domain.map.enums.MapSize;
import ua.hudyma.domain.map.enums.TownName;
import ua.hudyma.service.MapService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;

    @GetMapping("/randomlyPositionTowns")
    public ResponseEntity<List<TownDataDto>> randomlyPositionTowns(
            @RequestParam int townQuantity, MapSize mapSize) {
        return ResponseEntity.ok(mapService
                .randomlyPositionTowns(townQuantity, mapSize));
    }

    @PostMapping("/getNearestTown")
    public ResponseEntity<TownName> getNearestTown (
            @RequestBody MapCoordinatesDto heroPositionDto){
        return ResponseEntity.ok(mapService
                .getNearestTown(heroPositionDto));
    }
}
