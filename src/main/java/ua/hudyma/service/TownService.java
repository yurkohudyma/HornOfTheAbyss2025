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
        if (town.getPlayer() != incomingHero.getPlayer()){
            combatService.initTownBattle(incomingHero, town);
            log.error("Town is OCCUPIED by enemy player {}", town.getPlayer().getName());
            //todo if battle succeeds, proceed with allocation
        }
        else if (town.getVisitingHero() != null) {
            swapHeroesAtTownGarrison(town.getGarrisonHero(), incomingHero);
        }
        if (town.getGarrisonArmy() != null){ //todo in reality visiting incomingHero
            // todo does not upscale creature until garrison army has been transferred to him
            var upgradedGarrisonArmy = armyService
                    .upgradeArmySkillToHero(town.getGarrisonArmy(), incomingHero);
            town.setGarrisonArmy(upgradedGarrisonArmy);
        }
        town.setVisitingHero(incomingHero);
        return String.format("Hero %s is now visiting %s", incomingHero.getName(), town.getName());
    }

    private void swapHeroesAtTownGarrison(Hero garrisonHero, Hero hero) {
        throw new IllegalStateException("swapHeroesAtTownGarrison :: Method not implemented");
    }


    private Town getTown(String name) {
        return townRepository.findByName(name)
                .orElseThrow(MessageProcessor
                        .getExceptionSupplier(Town.class,
                                name,
                                EntityNotFoundException::new));
    }
}
