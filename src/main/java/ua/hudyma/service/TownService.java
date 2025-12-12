package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.dto.TownReqDto;
import ua.hudyma.domain.towns.enums.CastleDwellingType;
import ua.hudyma.domain.towns.enums.InitialBuildingConfig;
import ua.hudyma.domain.towns.enums.GrailBuildingType;
import ua.hudyma.mapper.TownMapper;
import ua.hudyma.mapper.TownRespDto;
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
        townRepository.save(town);
        return MessageProcessor
                .getReturnMessage(town, "name");
    }

    public TownRespDto fetchTown(String name) {
        var town = getTown(name);
        return townMapper.toDto(town);
    }

    private Town getTown(String name) {
        return townRepository.findByName(name)
                .orElseThrow(MessageProcessor
                        .getExceptionSupplier(Town.class,
                                name,
                                EntityNotFoundException::new));
    }
}
