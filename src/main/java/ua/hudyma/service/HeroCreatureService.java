package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.heroes.Hero;

@Service
@RequiredArgsConstructor
@Log4j2
public class HeroCreatureService {

    private final CreatureService creatureService;

    public Integer getHeroSlowestCreatureSpeedValue(Hero hero) {
        return creatureService.getHeroSlowestCreatureSpeedValue(hero.getCode());
    }
}
