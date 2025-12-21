package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.towns.dto.AbstractBuildReqDto;
import ua.hudyma.service.build.AbstractBuildService;

@RestController
@RequestMapping("/build")
@RequiredArgsConstructor
public class BuildController {
    private final AbstractBuildService abstractBuildService;

    @GetMapping("/detect")
    public ResponseEntity<String> detect(
            @RequestParam String type){
        return ResponseEntity.ok(abstractBuildService
                .resolveBuildingEnumType(type).getSimpleName());
    }

    @PostMapping
    public ResponseEntity<String> build(
            @RequestBody AbstractBuildReqDto dto){
        return ResponseEntity.ok(abstractBuildService
                .build(dto));
    }
}
