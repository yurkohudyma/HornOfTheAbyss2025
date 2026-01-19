package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import java.util.List;

import static ua.hudyma.domain.heroes.enums.PrimarySkill.POWER;
import static ua.hudyma.domain.spells.enums.SpellAction.*;

@Getter
@RequiredArgsConstructor
public enum EarthSpellSchool implements AbstractSpellSchool {
    ANTI_MAGIC (3, BUF, 0, null,
            1,
            List.of()),
    ANIMATE_DEAD(3, MISC, 0, null,
            1,
            List.of()),
    DEATH_RIPPLE(2,DAMAGE, 0, null,
            1,
            List.of()),
    EARTHQUAKE(3, MISC, 0, null,
            1,
            List.of()),
    FORCE_FIELD(3,MISC, 0, null,
            1,
            List.of()),
    IMPLOSION(5, DAMAGE, 30, POWER,
            75,
            List.of(100, 200, 300)),
    MAGIC_ARROW(1, DAMAGE, 6, POWER,
            1,
            List.of()),
    METEOR_SHOWER(4,DAMAGE, 0, null,
            1,
            List.of()),
    PROTECTION_FROM_EARTH(3, BUF, 0, null,
            1,
            List.of()),
    QUICKSAND(2,MISC, 0, null,
            1,
            List.of()),
    RESURRECTION(4,MISC, 0, null,
            1,
            List.of()),
    SHIELD(1,BUF, 0, null,
            1,
            List.of()),
    SLOW(1, DEBUF, 0, null,
            1,
            List.of()),
    SORROW(4, DEBUF, 0, null,
            1,
            List.of()),
    STONE_SKIN(1, BUF, 0, null,
            1,
            List.of()),
    SUMMON_EARTH_ELEMENTAL(5,SUMMON, 25, POWER,
            1,
            List.of()),
    TOWN_PORTAL(4, ADVENTURE, 0, null,
            1,
            List.of()),
    VIEW_EARTH(1,ADVENTURE, 0, null,
            1,
            List.of()),
    VISIONS(2, ADVENTURE, 0, null,
            1,
            List.of());
    private final int spellLevel;
    private final SpellAction spellAction;
    private final Integer manaCost;
    private final PrimarySkill spellPrimarySkill; // hero's primary skill accounted in spell damage calculation (mostly WISDOM)
    private final Integer modifierCoefficient;
    private final List<Integer> modifiedValuesList;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Integer getManaCost() {
        return manaCost;
    }

    @Override
    public PrimarySkill getSpellPrimarySkill() {
        return spellPrimarySkill;
    }

    @Override
    public Integer getModifierCoefficient() {
        return modifierCoefficient;
    }

    @Override
    public List<Integer> getModifiedValuesList() {
        return modifiedValuesList;
    }
}
