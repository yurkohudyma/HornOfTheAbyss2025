package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.repository.HeroRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class CheatService {

    private final HeroRepository heroRepository;
    private final HeroService heroService;
    private final HeroMapper heroMapper;

    public HeroRespDto engageCode(String cheatCode, String heroCode) {
        var hero = heroService.getHero(heroCode);
        var heroArmy = hero.getArmyList();
        //todo validate cheatCode
        return heroMapper.toDto(hero);
    }
}
