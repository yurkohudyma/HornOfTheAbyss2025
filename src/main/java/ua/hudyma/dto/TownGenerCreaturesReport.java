package ua.hudyma.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.Map;

public record TownGenerCreaturesReport(
        String townName,
        Map<CreatureType, Integer> generCreatureMap
) {
}
