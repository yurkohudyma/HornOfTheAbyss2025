package ua.hudyma.domain.spells.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.dto.HeroSkillSpellModifierDto;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.heroes.enums.SkillLevel;
import ua.hudyma.domain.spells.AbstractSpellSchool;

import java.util.EnumMap;
import java.util.List;

import static ua.hudyma.domain.heroes.enums.PrimarySkill.POWER;

@Getter
@RequiredArgsConstructor
public enum AirSpellSchool implements AbstractSpellSchool {
    MAGIC_ARROW(1,
            SpellAction.DAMAGE,
            6,
            toDto(
                    POWER,
                    10,
                    List.of(10, 20, 30))),
    // BASIC:  One enemy target unit receives ((Power x 10) + 10) damage.
    // ADV:    One enemy target unit receives ((Power x 10) + 20) damage.
    // EXPERT: One enemy target unit receives ((Power x 10) + 30) damage.
    DISGUISE(2, SpellAction.MISC, 0, null),
    PRECISION(2, SpellAction.BUF, 16, null), //real is 8
    VISIONS(2, SpellAction.MISC, 0, null);

    private final int spellLevel;
    private final SpellAction spellAction;
    private final Integer manaCost;
    private final HeroSkillSpellModifierDto heroSkillSpellModifierDto;

    private static HeroSkillSpellModifierDto toDto(
            PrimarySkill skill,
            Integer coefficient,
            List<Integer> modifList) {
        return new HeroSkillSpellModifierDto(
                skill,
                coefficient,
                toSkillLevelMap(modifList));
    }

    private static EnumMap<SkillLevel, Integer> toSkillLevelMap(
            List<Integer> modifiersList) {
        var enumMap = new EnumMap<SkillLevel, Integer>(SkillLevel.class);
        if (modifiersList.size() != 3) throw new IllegalArgumentException
                ("SkillLevel modifier List should have size of 3");
        var skillLevelCounter = 0;
        var values = SkillLevel.values();
        for (Integer integer : modifiersList) {
            enumMap.put(values[skillLevelCounter++], integer);
        }
        return enumMap;
    }

    @Override
    public int getSpellLevel() {
        return spellLevel;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Integer getManaCost() {
        return manaCost;
    }

    @Override
    public HeroSkillSpellModifierDto getHeroSkillSpellModifierDto (){
        return heroSkillSpellModifierDto;
    }
}
