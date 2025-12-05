package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.creatures.Creature;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureReqDto;
import ua.hudyma.domain.creatures.dto.CreatureRespDto;
import ua.hudyma.mapper.CreatureMapper;
import ua.hudyma.repository.CreatureRepository;
import ua.hudyma.util.MessageProcessor;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class CreatureService {
    private final CreatureRepository creatureRepository;
    private final CreatureMapper creatureMapper;

    @SneakyThrows
    public String createCreature(CreatureReqDto dto) {
        var creature = creatureMapper.toEntity(dto);
        creatureRepository.save(creature);
        return MessageProcessor.getReturnMessage(creature, "creatureType");
    }

    public CreatureRespDto fetchCreature(Long id) {
        return creatureMapper.toDto(getCreature(id));

    }

    public Creature fetchCreatureByType(CreatureType type) {
        return creatureRepository.findByCreatureType(type)
                .orElseThrow(getExceptionSupplier(Creature.class, type,
                EntityNotFoundException::new));
    }

    private Creature getCreature(Long id) {
        return creatureRepository
                .findById(id)
                .orElseThrow(getExceptionSupplier(Creature.class, id,
                        EntityNotFoundException::new));
    }
}
