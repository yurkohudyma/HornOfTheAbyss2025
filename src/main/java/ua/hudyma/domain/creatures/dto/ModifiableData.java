package ua.hudyma.domain.creatures.dto;

import lombok.Data;

@Data
public class ModifiableData {
    private Integer currentValue;
    private Integer modifiedValue;

    public ModifiableData(Integer currentValue, Integer modifiedValue) {
        this.currentValue = currentValue;
        this.modifiedValue = modifiedValue;
    }
}
