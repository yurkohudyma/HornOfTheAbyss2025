package ua.hudyma.domain.creatures.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.CreatureType;

@Getter
@RequiredArgsConstructor
public enum InfernoCreatureType implements CreatureType {
    ARCHDEVIL (7),
    FAMILIAR(1);

    private final Integer level;

    @Override
    public String getCode() {
        return name();
    }
}
