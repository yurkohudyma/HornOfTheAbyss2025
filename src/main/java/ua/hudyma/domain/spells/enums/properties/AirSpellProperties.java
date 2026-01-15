package ua.hudyma.domain.spells.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.enums.SpellAction;
import ua.hudyma.domain.spells.enums.SpellSchool;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static ua.hudyma.domain.heroes.enums.PrimarySkill.*;
import static ua.hudyma.domain.spells.enums.SpellSchool.AIR;

@Getter
@RequiredArgsConstructor
public enum AirSpellProperties implements AbstractSpellProperty {
    MAGIC_ARROW(AIR, emptyMap(), Set.of()),
    DISGUISE(AIR, emptyMap(), Set.of()),
    PRECISION(AIR, emptyMap(), Set.of()), //todo introduce secondarySkillMap,), //real is 8
    VISIONS(AIR,emptyMap(), Set.of());
    private final SpellSchool spellSchool;
    private final Map<PrimarySkill, Integer> skillModifierMap;
    private final Set<String> targetCreatureSet;

    private static <T extends Enum<T>> EnumMap<T, Integer> emptyMap() {
        return new EnumMap<>((Class<T>) PrimarySkill.class);
    }
    private static <T extends Enum<T>> EnumMap<T, Integer> toEnumMap(
            Class<T> enumClass, Map<T, Integer> resources) {
        var map = new EnumMap<T, Integer>(enumClass);
        map.putAll(resources);
        return map;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public SpellSchool getSpellSchool(){
        return spellSchool;
    }

}
