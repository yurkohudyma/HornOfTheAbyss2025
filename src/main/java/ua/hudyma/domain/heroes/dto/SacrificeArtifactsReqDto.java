package ua.hudyma.domain.heroes.dto;

public record SacrificeArtifactsReqDto(
        String heroId,
        String [] artifactsArray
) {
}
