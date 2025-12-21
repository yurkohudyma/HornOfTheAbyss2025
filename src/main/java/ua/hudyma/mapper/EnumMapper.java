package ua.hudyma.mapper;

import org.springframework.stereotype.Component;
import ua.hudyma.domain.creatures.BaseCreatureSkill;

import java.util.Arrays;
import java.util.Optional;

public class EnumMapper {
    public static <
            S extends Enum<S> & BaseCreatureSkill,
            T extends Enum<T> & BaseCreatureSkill>
    Optional<T> map(S source, Class<T> targetEnum) {
        return Arrays.stream(targetEnum.getEnumConstants())
                .filter(t -> t.getCode().equals(source.getCode()))
                .findFirst();
    }
}

