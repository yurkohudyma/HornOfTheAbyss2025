package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor public enum HallType {
    VILLAGE_HALL (500),
    TOWN_HALL (1000),
    CITY_HALL (2000),
    CAPITOL (4000);

    private final Integer income;

    }
