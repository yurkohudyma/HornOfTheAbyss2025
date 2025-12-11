package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.BuildingType;
import ua.hudyma.domain.towns.enums.GrailBuildingType;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.repository.TownRepository;
import ua.hudyma.util.MessageProcessor;

@Service
@RequiredArgsConstructor
@Log4j2
public class TownService {
    private final TownRepository townRepository;
    private final TownMapper townMapper;

    @SneakyThrows
    public String createTown(TownReqDto dto){
        var town = townMapper.toEntity(dto);
        GrailBuildingType grailBuilding = BuildingType
                .grailBuilding;
        return MessageProcessor
                .getReturnMessage(town, "name");
    }
}
