package ua.hudyma.domain.players.dto;

import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.players.enums.PlayerColour;

import java.util.List;

public record PlayerRespDto(
        String name,
        PlayerColour playerColour,
        List<HeroRespDto> heroDtosList
) {
}
