package ua.hudyma.domain.heroes.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SkillLevel {
    BASIC(1), ADVANCED(2), EXPERT(3);
    private final Integer level;
}
