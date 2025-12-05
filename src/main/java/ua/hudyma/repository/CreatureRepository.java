package ua.hudyma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.hudyma.domain.creatures.Creature;
import ua.hudyma.domain.creatures.CreatureType;

import java.util.Optional;

public interface CreatureRepository extends JpaRepository<Creature, Long> {
    Optional<Creature> findByCreatureType(CreatureType type);
}
