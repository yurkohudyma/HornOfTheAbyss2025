package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.dto.ModifiableData;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.exception.EnumMappingErrorException;
import ua.hudyma.mapper.EnumMapper;

import java.util.EnumMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ArmyHeroService {
    private final CreatureService creatureService;
    //private final ArmyService armyService;

    public List<CreatureSlot> syncArmySkillsWithHero(
            List<CreatureSlot> armyList,
            Hero hero) {
        armyList.forEach(armyslot -> {
                    var modifiableMap = new EnumMap<ModifiableSkill, ModifiableData>
                            (ModifiableSkill.class);
                    var skillEnums = ModifiableSkill.values();
                    var regularCreatureSkillMap =
                            creatureService.fetchCreatureByType(armyslot.getType())
                                    .getCreatureSkillMap();
                    for (ModifiableSkill skill : skillEnums) {
                        var regularSkillValue = regularCreatureSkillMap
                                .get(EnumMapper
                                        .map(skill, CreatureSkill.class)
                                        .orElseThrow(() -> new EnumMappingErrorException
                                                ("Error mapping to " +
                                                        CreatureSkill.class.getSimpleName())))
                                .get(0);
                        modifiableMap.put(skill, new ModifiableData(
                                        regularSkillValue.value(),
                                        ArmyService.getModifiedValue(hero, skill, regularSkillValue)
                                )
                        );
                    }
                    armyslot.setModifiableDataMap(modifiableMap);
                }
        );
        return armyList;
    }

    public List<CreatureSlot> syncArmySkillsWithHero (Hero hero){
        return syncArmySkillsWithHero(hero.getArmyList(), hero);
    }
}
