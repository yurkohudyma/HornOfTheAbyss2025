package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.dto.HeroSkillSpellModifierDto;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import static ua.hudyma.domain.spells.enums.SpellAction.*;

@Getter
@RequiredArgsConstructor
public enum EarthSpellSchool implements AbstractSpellSchool {
    ANTI_MAGIC (3, BUF, 0),
    ANIMATE_DEAD(3, MISC, 0),
    DEATH_RIPPLE(2,DAMAGE, 0),
    EARTHQUAKE(3, MISC, 0),
    FORCE_FIELD(3,MISC, 0),
    IMPLOSION(5, DAMAGE, 30),
    MAGIC_ARROW(1, DAMAGE, 0),
    METEOR_SHOWER(4,DAMAGE, 0),
    PROTECTION_FROM_EARTH(3, BUF, 0),
    QUICKSAND(2,MISC, 0),
    RESURRECTION(4,MISC, 0),
    SHIELD(1,BUF, 0),
    SLOW(1, DEBUF, 0),
    SORROW(4, DEBUF, 0),
    STONE_SKIN(1, BUF, 0),
    SUMMON_EARTH_ELEMENTAL(5,MISC, 0),
    TOWN_PORTAL(4, ADVENTURE, 0),
    VIEW_EARTH(1,ADVENTURE, 0),
    VISIONS(2, ADVENTURE, 0);
    private final int spellLevel;
    private final SpellAction spellAction;
    private final Integer manaCost;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Integer getManaCost() {
        return manaCost;
    }

    @Override
    public HeroSkillSpellModifierDto getHeroSkillSpellModifierDto() {
        return null;
    }
}
