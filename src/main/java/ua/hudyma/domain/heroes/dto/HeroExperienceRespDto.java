package ua.hudyma.domain.heroes.dto;

public record HeroExperienceRespDto(
        String heroName,
        Integer level,
        Integer experience
) {
}
