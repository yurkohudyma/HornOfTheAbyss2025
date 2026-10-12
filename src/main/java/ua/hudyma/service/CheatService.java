package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.cheats.enums.Cheats;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.HeroParams;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.players.dto.PlayerRespDto;
import ua.hudyma.domain.resource.enums.ResourceType;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.mapper.PlayerMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static ua.hudyma.domain.creatures.enums.creaturetypes.CastleCreatureType.ARCHANGEL;
import static ua.hudyma.domain.creatures.enums.creaturetypes.NecropolisCreatureType.BLACK_KNIGHT;

@Service
@RequiredArgsConstructor
@Log4j2
public class CheatService {
    private final HeroService heroService;
    private final SpellService spellService;
    private final int MAX_ARMY_SLOT_SIZE = 7;
    private final List<Cheats> cheatList = Arrays.stream(Cheats.values()).toList();
    private final HeroMapper heroMapper;

    public Object engageCode(String cheatCode, String heroCode) {
        if (cheatCode.isEmpty() || cheatCodeIsUnknown(cheatCode)) {
            throw new IllegalArgumentException("Cheatcode" + cheatCode + " is not recognized");
        }
        var hero = heroService.getHero(heroCode);
        var player = hero.getPlayer();
        return switch (cheatCode) {
            case "nwctrinity" -> supplyTroops(hero, ARCHANGEL, 5);
            case "nwcagents" -> supplyTroops(hero, BLACK_KNIGHT, 10);
            case "nwctheconstruct" -> construct(player);
            case "nwcthereisnospoon" -> spoon(hero);
            default -> throw new IllegalArgumentException("cheatcode " + cheatCode + " not implemented yet");
        };
    }

    //@Transactional
    // disabled for testing
    private HeroRespDto spoon(Hero hero) {
        var allSpellSet = spellService.getAllSpells();
        var allSpellMap = spellService
                .convertSpellSetIntoSpellMap(allSpellSet);
        var allSpellsSetMap = spellService
                .convertListSpellMapIntoSetSpellMap(allSpellMap);
        hero.setSpellBook(allSpellsSetMap);
        var parametersMap = heroService.getOrCreateHeroParamsMap(hero);
        parametersMap.put(HeroParams.CUR_SPELL_POINTS, 999);
        return heroMapper.toDto(hero);
    }

    //@Transactional
    // disabled for testing
    private Map<ResourceType, Integer> construct(Player player) {        
        var resourceMap = player.getResourceMap();
        resourceMap.merge(ResourceType.GOLD, 100_000, Integer::sum);
        resourceMap
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(ResourceType.GOLD))
                .forEach(entry ->
                        resourceMap
                        .merge(entry.getKey(),
                                100,
                                Integer::sum));
        return resourceMap;
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
        return !cheatList.contains(Cheats.valueOf(cheatCode.toLowerCase()));
    }
}
