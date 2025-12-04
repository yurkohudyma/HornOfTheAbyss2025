package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.exception.ArmyFreeSlotOverflowException;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.repository.HeroRepository;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;
import static ua.hudyma.util.MessageProcessor.getReturnMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class HeroService {
    private final HeroMapper heroMapper;
    private final HeroRepository heroRepository;
    private final Integer ARMY_SLOT_MAX_QTY = 7;

    @SneakyThrows
    @Transactional
    public String createHero(HeroReqDto dto) {
        var hero = heroMapper.toEntity(dto);
        heroRepository.save(hero);
        return getReturnMessage(hero, "name");
    }

    public HeroRespDto fetchHero(String code) {
        var hero = getHero(code);
        return heroMapper.toDto(hero);
    }

    public Hero getHero (String heroCode){
        return heroRepository.findByCode(heroCode)
                .orElseThrow(getExceptionSupplier(
                        Hero.class,
                        heroCode,
                        EntityNotFoundException::new));
    }

    @Transactional
    public String reinforceArmy(ReinforceReqDto dto) {
        var hero = getHero(dto.heroCode());
        var heroCurrentArmyMap = hero.getArmyMap();
        var requestedArmyMap = dto.armyMap();
        if (heroCurrentArmyMap == null) {
            hero.setArmyMap(requestedArmyMap);
        }
        else if (ARMY_SLOT_MAX_QTY - heroCurrentArmyMap.size() <
                 requestedArmyMap.size()){
            throw new
                    ArmyFreeSlotOverflowException("Current "+ hero.getName() +
                    "'s army HAS no vacant slots");
        }
        else {
            hero.getArmyMap().putAll(requestedArmyMap);
        }
        return String.format("%s's army has been reinforced with %d units", hero.getName(), requestedArmyMap.size());
    }

    //todo cannot add identical key to map, therefore
    /*"ARCHANGEL": 1,
            "ARCHDEVIL": 2,
            "ARCHANGEL": 3,
            "ARCHDEVIL": 4,
            "ARCHANGEL": 5*/
    //would not be saved

    //todo переробити на List<CreatureSlot>, така собі ідея....
    // todo Але можна тулити однакові юніти
}
