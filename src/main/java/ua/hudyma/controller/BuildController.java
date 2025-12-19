package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.towns.dto.BuildReqDto;
import ua.hudyma.service.build.CommonBuildService;

@RestController
@RequestMapping("/build")
@RequiredArgsConstructor
public class BuildController {
    private final CommonBuildService commonBuildService;

    @GetMapping("/detect")
    public ResponseEntity<String> detect(@RequestParam String type){
        return ResponseEntity.ok(commonBuildService
                .resolveBuildingEnumType(type).getSimpleName());
    }

    @PostMapping("/common")
    public ResponseEntity<String> build(@RequestBody BuildReqDto dto){
        return ResponseEntity.ok(commonBuildService
                .build(dto));
    }
}
