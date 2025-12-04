package ua.hudyma.mapper;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.HeroReqDto;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.service.PlayerService;
import ua.hudyma.util.IdGenerator;

import static ua.hudyma.util.IdGenerator.generateId;

@Component
@RequiredArgsConstructor
public class HeroMapper extends BaseMapper<HeroRespDto, Hero, HeroReqDto> {
    private final PlayerService playerService;

    @Override
    public HeroRespDto toDto(Hero hero) {
        return new HeroRespDto(
                hero.getName(),
                hero.getPlayer().getName(),
                hero.getCode(),
                hero.getFaction(),
                hero.getSubfaction(),
                hero.getPrimarySkillMap(),
                hero.getSecondarySkillMap(),
                hero.getBodyInventoryMap(),
                hero.getMiscInventoryMap(),
                hero.getBackpackInventoryList()
        );
    }

    @Override
    public Hero toEntity(HeroReqDto dto) {
        var player = playerService.getPlayer(dto.playerId());
        return Hero
                .builder()
                .name(dto.name())
                .code( generateId(1,3))
                .player(player)
                .faction(dto.faction())
                .subfaction(dto.subfaction())
                .primarySkillMap(dto.primarySkillMap())
                .secondarySkillMap(dto.secondarySkillMap())
                .bodyInventoryMap(dto.bodyInventoryMap())
                .miscInventoryMap(dto.miscInventoryMap())
                .backpackInventoryList(dto.backpackInventoryList())
                .build();
    }
}
