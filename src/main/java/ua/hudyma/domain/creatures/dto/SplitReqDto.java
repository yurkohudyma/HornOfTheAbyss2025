package ua.hudyma.domain.creatures.dto;

public record SplitReqDto(
        String heroId,
        String slotId,
        Integer splitQuantity
) {
}
