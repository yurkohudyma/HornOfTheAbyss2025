package ua.hudyma.mapper;

import org.springframework.stereotype.Component;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.heroes.dto.CreatureSlotReqDto;
import ua.hudyma.domain.heroes.dto.CreatureSlotRespDto;

@Component public class ArmyMapper extends BaseMapper<CreatureSlotRespDto, CreatureSlot, CreatureSlotReqDto> {
    @Override
    public CreatureSlotRespDto toDto(CreatureSlot creatureSlot) {
        return new CreatureSlotRespDto(
                creatureSlot.getSlotId(),
                creatureSlot.getType(),
                creatureSlot.getQuantity()
        );
    }

    @Override
    public CreatureSlot toEntity(CreatureSlotReqDto creatureSlotReqDto) {
        throw new IllegalStateException("Method NOT implemented");
    }
}
