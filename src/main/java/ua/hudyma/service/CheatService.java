package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.cheats.enums.Cheats;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.heroes.Hero;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType.ARCHANGEL;
import static ua.hudyma.domain.creatures.enums.creaturetypes.NecropolisCreatureType.BLACK_KNIGHT;

@Service
@RequiredArgsConstructor
@Log4j2
public class CheatService {
    private final HeroService heroService;
    private final int MAX_ARMY_SLOT_SIZE = 7;
    private final List<Cheats> cheatList = Arrays.stream(Cheats.values()).toList();

    public List<CreatureSlot> engageCode(String cheatCode, String heroCode) {
        if (cheatCode.isEmpty() || cheatCodeIsUnknown(cheatCode)) {
            throw new IllegalArgumentException("Cheatcode" + cheatCode + " is not recognized");
        }
        var hero = heroService.getHero(heroCode);
        return switch (cheatCode) {
            case "nwctrinity" -> supplyTroops(hero, ARCHANGEL, 5);
            case "nwcagents" -> supplyTroops(hero, BLACK_KNIGHT, 10);
            default -> throw new IllegalArgumentException("cheatcode " + cheatCode + " not implemented yet");
        };
    }

    //@Transactional
    // disabled for testing
    private List<CreatureSlot> supplyTroops(
            Hero hero,
            CreatureType creatureType,
            int quantity) {
        var heroArmy = hero.getArmyList();
        var vacantArmySize = MAX_ARMY_SLOT_SIZE - heroArmy.size();
        IntStream.range(0, Math.max(0, vacantArmySize))
                .forEach(i -> heroArmy.add(
                        new CreatureSlot(creatureType, quantity)
                ));
        return heroArmy;
    }

    private boolean cheatCodeIsUnknown(String cheatCode) {
        return !cheatList.contains(Cheats.valueOf(cheatCode));
    }
}
