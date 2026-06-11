package ua.hudyma.domain.heroes.dto;

public record MovemementPointsRespDto(
        Integer landMovePoints,
        Integer maxLandMovePoints,
        Integer waterMovePoints,
        Integer maxWaterMovePoints) {
}
