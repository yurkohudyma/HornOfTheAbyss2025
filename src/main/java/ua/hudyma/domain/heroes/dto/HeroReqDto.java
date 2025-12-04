package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.artifacts.enums.Artifact;
import ua.hudyma.domain.heroes.enums.*;

import java.util.List;
import java.util.Map;

public record HeroReqDto(
        String name,
        Long playerId,
        HeroFaction faction,
        HeroSubfaction subfaction,
        Map<PrimarySkill, Integer> primarySkillMap,
        Map<SecondarySkill, SkillLevel> secondarySkillMap,
        Map<ArtifactSlot, List<Artifact>> bodyInventoryMap,
        Map<ArtifactSlot, Artifact> miscInventoryMap,
        List<Artifact> backpackInventoryList
) {}
