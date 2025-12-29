package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import static ua.hudyma.domain.spells.enums.SpellAction.*;

@Getter
@RequiredArgsConstructor
public enum EarthSpellSchool implements AbstractSpellSchool {
    ANTI_MAGIC (3, BUF),
    ANIMATE_DEAD(3, MISC),
    DEATH_RIPPLE(2,DAMAGE),
    EARTHQUAKE(3, MISC),
    FORCE_FIELD(3,MISC),
    IMPLOSION(5, DAMAGE),
    MAGIC_ARROW(1, DAMAGE),
    METEOR_SHOWER(4,DAMAGE),
    PROTECTION_FROM_EARTH(3, BUF),
    QUICKSAND(2,MISC),
    RESURRECTION(4,MISC),
    SHIELD(1,BUF),
    SLOW(1, DEBUF),
    SORROW(4, DEBUF),
    STONE_SKIN(1, BUF),
    SUMMON_EARTH_ELEMENTAL(5,MISC),
    TOWN_PORTAL(4, ADVENTURE),
    VIEW_EARTH(1,ADVENTURE),
    VISIONS(2, ADVENTURE);
    private final int spellLevel;
    private final SpellAction spellAction;

    @Override
    public String getName() {
        return name();
    }
}
