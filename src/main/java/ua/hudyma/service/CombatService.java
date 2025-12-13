package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;

@Service
@RequiredArgsConstructor
@Log4j2
public class CombatService {
    public void initTownBattle(Hero hero, Town town) {
        throw new IllegalStateException("initTownBattle :: Method not implemented");
    }
}
