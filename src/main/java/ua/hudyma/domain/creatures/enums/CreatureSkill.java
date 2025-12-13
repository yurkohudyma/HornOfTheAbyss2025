package ua.hudyma.domain.creatures.enums;

import ua.hudyma.domain.creatures.BaseCreatureSkill;

public enum CreatureSkill implements BaseCreatureSkill {
    ATTACK,
    DEFENSE,
    HEALTH,
    DAMAGE,
    SPEED,
    GROWTH,
    SIZE,
    SHOTS;

    @Override
    public String getCode() {
        return name();
    }
}
