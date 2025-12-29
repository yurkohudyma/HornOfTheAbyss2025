package ua.hudyma.domain.spells.enums.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.domain.towns.enums.CommonBuildingType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static ua.hudyma.domain.heroes.enums.PrimarySkill.*;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.KNOWLEDGE;

@Getter
@RequiredArgsConstructor
public enum FireSpellProperties implements AbstractSpellProperty {
    ARMAGEDDON(emptyMap(), Set.of()),
    SLAYER(toEnumMap(PrimarySkill.class,
            Map.of(
                    ATTACK, 8,
                    DEFENSE, 8,
                    POWER, 8,
                    KNOWLEDGE, 8)),
            Set.of("BEHEMOTH", "DRAGON", "HYDRA"));
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

}
