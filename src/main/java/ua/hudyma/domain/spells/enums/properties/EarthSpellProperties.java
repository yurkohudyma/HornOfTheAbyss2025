package ua.hudyma.domain.spells.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.enums.SpellSchool;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static ua.hudyma.domain.spells.enums.SpellSchool.EARTH;

@Getter
@RequiredArgsConstructor
public enum EarthSpellProperties implements AbstractSpellProperty {

    SUMMON_EARTH_ELEMENTAL(EARTH,
            emptyMap(),
            Set.of()),

    IMPLOSION(EARTH,
            emptyMap(),
            Set.of());

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
