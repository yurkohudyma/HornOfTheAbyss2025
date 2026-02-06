package ua.hudyma.domain.spells.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.enums.AttackType;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.spells.enums.SpellSchool;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static ua.hudyma.domain.creatures.enums.AttackType.FIREBALL_STYLE;
import static ua.hudyma.domain.creatures.enums.AttackType.SHOOTING;
import static ua.hudyma.domain.spells.enums.SpellSchool.AIR;

@Getter
@RequiredArgsConstructor
public enum AirSpellProperties implements AbstractSpellProperty {
    MAGIC_ARROW(AIR, emptyMap(), Set.of(), Set.of()),
    DISGUISE(AIR, emptyMap(), Set.of(), Set.of()),
    PRECISION(AIR, emptyMap(), Set.of(), Set.of(
            SHOOTING,
            FIREBALL_STYLE)), //real is 8
    VISIONS(AIR,emptyMap(), Set.of(), Set.of()),
    SUMMON_AIR_ELEMENTAL (AIR, emptyMap(), Set.of(), Set.of());
    private final SpellSchool spellSchool;
    private final Map<PrimarySkill, Integer> skillModifierMap;
    private final Set<String> targetCreatureSet;
    private final Set<AttackType> effectedActivityType;

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

    @Override
    public Set<AttackType> getEffectedActivityType() {
        return effectedActivityType;
    }
}
