package ua.hudyma.dto;

import ua.hudyma.enums.WarMachine;
import ua.hudyma.enums.WarMachineProperties;

public record WarMachineRespDto(
        WarMachine warMachine,
        Integer attack,
        Integer defense,
        Integer minDamage,
        Integer maxDamage
) {

}
