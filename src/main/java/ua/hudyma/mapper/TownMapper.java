package ua.hudyma.mapper;

import org.springframework.stereotype.Component;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.TownReqDto;

@Component
public class TownMapper extends BaseMapper<TownRespDto, Town, TownReqDto> {
    @Override
    public TownRespDto toDto(Town town) {
        return null;
    }

    @Override
    public Town toEntity(TownReqDto townReqDto) {
        return null;
    }
}
