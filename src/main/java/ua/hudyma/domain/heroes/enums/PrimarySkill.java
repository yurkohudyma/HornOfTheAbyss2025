package ua.hudyma.domain.heroes.enums;

import ua.hudyma.domain.creatures.BaseCreatureSkill;

public enum PrimarySkill implements BaseCreatureSkill {
    ATTACK,
    DEFENSE,
    POWER,
    KNOWLEDGE;

    @Override
    public String getCode() {
        return name();
    }
}
