package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.cheats.enums.Cheats;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.mapper.HeroMapper;

import java.util.Arrays;
import java.util.List;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType.ARCHANGEL;
import static ua.hudyma.domain.creatures.enums.creaturetypes.NecropolisCreatureType.BLACK_KNIGHT;

@Service
@RequiredArgsConstructor
@Log4j2
public class CheatService {
    private final HeroService heroService;
    private final HeroMapper heroMapper;
    private final List<Cheats> cheatList = Arrays.stream(Cheats.values()).toList();

    public HeroRespDto engageCode(String cheatCode, String heroCode) {
        var hero = heroService.getHero(heroCode);
        if (cheatCode.isEmpty() || cheatCodeIsUnknown(cheatCode))
            throw new IllegalArgumentException("Cheatcode " + cheatCode + " is not recognized");
        return switch (cheatCode) {
            case "nwctrinity" -> supplyTroops(hero, ARCHANGEL);
            case "nwcagents" -> supplyTroops(hero, BLACK_KNIGHT);
            default -> throw new IllegalArgumentException("cheatcode " + cheatCode + " not implemented yet");
        };
    }

    private HeroRespDto supplyTroops(Hero hero, CreatureType creatureType) {
        //todo implement
        throw new IllegalArgumentException("Method not implemented yet");
    }

    private boolean cheatCodeIsUnknown(String cheatCode) {
        return !cheatList.contains(Cheats.valueOf(cheatCode));
    }
}
