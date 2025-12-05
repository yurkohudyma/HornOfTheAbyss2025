package ua.hudyma.domain.creatures.enums;

import ua.hudyma.domain.creatures.BaseCreatureSkill;

public enum ModifiableSkill implements BaseCreatureSkill {
    ATTACK,
    DEFENSE,
    HEALTH,
    DAMAGE,
    SPEED;

    @Override
    public String getCode() {
        return name();
    }
}
