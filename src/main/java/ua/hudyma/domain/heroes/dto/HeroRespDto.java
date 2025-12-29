package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.artifacts.enums.ArtifactSlotDisposition;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.heroes.enums.*;

import java.util.List;
import java.util.Map;

public record HeroRespDto(
        String playerName,
        String name,
        String code,
        HeroFaction faction,
        HeroSubfaction subfaction,
        Map<PrimarySkill, Integer> primarySkillMap,
        Map<ua.hudyma.domain.heroes.HeroParams, Integer> parametersMap, Map<SecondarySkill, SkillLevel> secondarySkillMap,
        Map<ArtifactSlot, ArtifactSlotDisposition> bodyInventoryMap,
        Map<ArtifactSlot, ArtifactSlotDisposition> miscInventoryMap,
        List<ArtifactSlotDisposition> backpackInventoryList,
        List<CreatureSlot> armySlotList

) {
}
