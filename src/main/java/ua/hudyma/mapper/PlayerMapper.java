package ua.hudyma.mapper;

import org.springframework.stereotype.Component;
import ua.hudyma.domain.Player;
import ua.hudyma.domain.players.dto.PlayerReqDto;
import ua.hudyma.domain.players.dto.PlayerRespDto;

@Component
public class PlayerMapper extends BaseMapper<PlayerRespDto, Player, PlayerReqDto> {
    @Override
    public PlayerRespDto toDto(Player player) {
        return new PlayerRespDto(player.getName());
    }

    @Override
    public Player toEntity(PlayerReqDto dto) {
        var player = new Player();
        player.setName(dto.name());
        return player;
    }
}
