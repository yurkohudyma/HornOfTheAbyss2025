package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.domain.towns.dto.TownRespDto;
import ua.hudyma.repository.TownRepository;
import ua.hudyma.util.MessageProcessor;

@Service
@RequiredArgsConstructor
@Log4j2
public class TownService {
    private final TownRepository townRepository;
    private final TownMapper townMapper;
    private final HeroService heroService;
    private final CombatService combatService;
    private final ArmyService armyService;

    @SneakyThrows
    public String createTown(TownReqDto dto){
        var town = townMapper.toEntity(dto);
        townRepository.save(town);
        return MessageProcessor
                .getReturnMessage(town, "name");
    }

    public TownRespDto fetchTown(String name) {
        var town = getTown(name);
        return townMapper.toDto(town);
    }

    @Transactional
    public String allocateVisitingHero (String heroId, String townName){
        var town = getTown(townName);
        var incomingHero = heroService.getHero(heroId);
        var visitingHero = town.getVisitingHero();
        if (town.getPlayer() != incomingHero.getPlayer()){
            combatService.initTownBattle(incomingHero, town);
            log.error("Town is OCCUPIED by enemy player {}", town.getPlayer().getName());
            //todo if battle succeeds, proceed with allocation
        }
        else if (incomingHero == visitingHero || incomingHero == town.getGarrisonHero()){
            return String.format("Hero %s is ALREADY in %s",
                    incomingHero.getName(), town.getName());
        }
        else if (visitingHero != null) {
            swapHeroesAtTownGarrison(incomingHero, visitingHero, town);
            if (town.getGarrisonArmy() != null) { //todo in reality visiting incomingHero
                // todo does not upscale creature until garrison army has been transferred to him
                upgradeGarnisonSkillsByHero(town, incomingHero);
            }
            return String.format("Hero %s is now garnisoned in %s, while %s is Visitor",
                    incomingHero.getName(), town.getName(), visitingHero.getName());
        }
        else {
            town.setVisitingHero(incomingHero);
        }
        return String.format("Hero %s is now visiting %s", incomingHero.getName(), town.getName());
    }

    private void upgradeGarnisonSkillsByHero(Town town, Hero hero) {
        var upgradedGarrisonArmy = armyService
                .upgradeArmySkillToHero(town.getGarrisonArmy(), hero);
        town.setGarrisonArmy(upgradedGarrisonArmy);
    }


    private void swapHeroesAtTownGarrison(Hero incomingHero, Hero visitingHero, Town town) {
        town.setGarrisonHero(visitingHero);
        town.setVisitingHero(incomingHero);
        log.info("Hero {} is now garnisoned in {}, while {} is Visitor",
                town.getGarrisonHero().getName(),
                town.getName(),
                town.getVisitingHero().getName());
    }

    @Transactional
    public String swapHeroesInTown(String townName) {
        var town = getTown(townName);
        var visitor = town.getVisitingHero();
        var garnisoner = town.getGarrisonHero();
        if (visitor == null || garnisoner == null){
            return "Either visiting or garnison hero is missing";
        }
        swapHeroesAtTownGarrison(garnisoner, visitor, town);
        upgradeGarnisonSkillsByHero(town, visitor);
        return String.format("Heroes [%s <-- --> %s] rotated in town",
                garnisoner.getName(),
                visitor.getName());
    }

    private Town getTown(String name) {
        return townRepository.findByName(name)
                .orElseThrow(MessageProcessor
                        .getExceptionSupplier(Town.class,
                                name,
                                EntityNotFoundException::new));
    }
}
