package ua.hudyma.domain.creatures.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

@Getter
@RequiredArgsConstructor
public enum CastleCreatureType implements CreatureType {
    ARCHANGEL(7),
    CHAMPION(6),
    ZEALOT(5),
    CRUSADER(4),
    ROYAL_GRIFFIN(3),
    MARKSMAN(2),
    HALBERDIER (1);
    private final Integer level;

    @Override
    public String getCode() {
        return name();
    }
}
