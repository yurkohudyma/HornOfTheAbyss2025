package ua.hudyma.mapper;

import org.springframework.stereotype.Component;
import ua.hudyma.domain.creatures.Creature;
import ua.hudyma.domain.creatures.dto.CreatureReqDto;
import ua.hudyma.domain.creatures.dto.CreatureRespDto;

@Component
public class CreatureMapper extends BaseMapper<CreatureRespDto, Creature, CreatureReqDto> {
    @Override
    public CreatureRespDto toDto(Creature creature) {
        return new CreatureRespDto(
                creature.getId(),
                creature.getFaction(),
                creature.getCreatureType(),
                creature.getCreaturePropertyMap(),
                creature.getCreatureSkillMap()

        );
    }

    @Override
    public Creature toEntity(CreatureReqDto dto) {
        var creature = new Creature();
        creature.setFaction(dto.faction());
        creature.setCreatureType(dto.creatureType());
        creature.setCreaturePropertyMap(dto.creaturePropertyMap());
        creature.setCreatureSkillMap(dto.creatureSkillMap());
        return creature;
    }
}
