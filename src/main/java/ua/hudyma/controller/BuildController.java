package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.towns.dto.AbstractBuildReqDto;
import ua.hudyma.service.build.AbstractBuildService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BuildController {
    private final AbstractBuildService abstractBuildService;

    @GetMapping("/build/detect")
    public ResponseEntity<String> detect(
            @RequestParam String type){
        return ResponseEntity.ok(abstractBuildService
                .resolveBuildingEnumType(type).getSimpleName());
    }

    @PostMapping("/build")
    public ResponseEntity<String> build(
            @RequestBody AbstractBuildReqDto dto){
        return ResponseEntity.ok(abstractBuildService
                .build(dto));
    }
    @PostMapping("/build/dwell")
    public ResponseEntity<String> buildDwell(
            @RequestBody AbstractBuildReqDto dto){
        return ResponseEntity.ok(abstractBuildService
                .buildDwelling(dto));
    }
    @PatchMapping("/destroy")
    public ResponseEntity<String> destroyBuilding (
            @RequestParam String buildingType, @RequestParam String townName){
        return ResponseEntity.ok(abstractBuildService
                .destroyBuilding(buildingType, townName));
    }
}
