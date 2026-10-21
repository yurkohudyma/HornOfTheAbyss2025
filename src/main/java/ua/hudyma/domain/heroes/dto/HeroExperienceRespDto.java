package ua.hudyma.domain.heroes.dto;

public record HeroExperienceRespDto(
        String heroName,
        Integer currentLevel,
        Integer nextLevel,
        Integer currentExperience,
        Integer gainedExperience
) {
}
