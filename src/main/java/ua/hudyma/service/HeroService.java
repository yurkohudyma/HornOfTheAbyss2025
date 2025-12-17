package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.dto.ModifiableData;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.exception.ArmyFreeSlotOverflowException;
import ua.hudyma.exception.EnumMappingErrorException;
import ua.hudyma.mapper.EnumMapper;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.repository.HeroRepository;

import java.util.HashMap;
import java.util.List;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;
import static ua.hudyma.util.MessageProcessor.getReturnMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class HeroService {
    private final HeroMapper heroMapper;
    private final HeroRepository heroRepository;
    private final PlayerService playerService;

    @SneakyThrows
    @Transactional
    public String createHero(HeroReqDto dto) {
        var hero = heroMapper.toEntity(dto);
        var player = playerService.getPlayer(dto.playerId());
        hero.setPlayer(player);
        heroRepository.save(hero);
        return getReturnMessage(hero, "name");
    }

    public HeroRespDto fetchHero(String code) {
        var hero = getHero(code);
        return heroMapper.toDto(hero);
    }

    public List<HeroRespDto> fetchHeroDtoList (List<Hero> heroList){
        return heroMapper.toDtoList(heroList);
    }

    public Hero getHero(String heroCode) {
        return heroRepository.findByCode(heroCode)
                .orElseThrow(getExceptionSupplier(
                        Hero.class,
                        heroCode,
                        EntityNotFoundException::new));
    }

    public void vanquishHero(Hero hero) {
        heroRepository.delete(hero);
    }
}
