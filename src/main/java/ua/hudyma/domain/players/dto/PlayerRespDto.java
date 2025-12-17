package ua.hudyma.domain.players.dto;

import ua.hudyma.domain.heroes.dto.HeroRespDto;

import java.util.List;

public record PlayerRespDto(
        String name,
        List<HeroRespDto> heroDtosList
) {
}
