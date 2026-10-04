package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.service.CheatService;

@RestController
@RequestMapping("/cheats")
@RequiredArgsConstructor
public class CheatController {
    private final CheatService cheatService;

    @GetMapping
    public HeroRespDto engageCheat (
            @RequestParam String cheatCode,
            @RequestParam String heroCode){
        return cheatService.engageCode(cheatCode, heroCode);
    }

}
