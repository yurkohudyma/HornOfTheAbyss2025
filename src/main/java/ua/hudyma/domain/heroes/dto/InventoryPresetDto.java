package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.artifact.ArtifactSlotDisposition;
import ua.hudyma.domain.heroes.enums.ArtifactSlot;

import java.util.Map;

public record InventoryPresetDto(
        Integer index,
        Map<ArtifactSlot, ArtifactSlotDisposition> bodyInventoryMap,
        Map<ArtifactSlot, ArtifactSlotDisposition> miscInventoryMap
) {

}
