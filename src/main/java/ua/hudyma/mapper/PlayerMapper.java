package ua.hudyma.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.players.dto.PlayerReqDto;
import ua.hudyma.domain.players.dto.PlayerRespDto;
import ua.hudyma.service.HeroService;

@Component
@RequiredArgsConstructor
public class PlayerMapper extends BaseMapper<PlayerRespDto, Player, PlayerReqDto> {

    private final HeroMapper heroMapper;
    @Override
    public PlayerRespDto toDto(Player player) {
        return new PlayerRespDto(player.getName(),
                heroMapper
                        .toDtoList(player.getHeroList()));
    }

    @Override
    public Player toEntity(PlayerReqDto dto) {
        var player = new Player();
        player.setName(dto.name());
        return player;
    }
}
