package ua.hudyma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.hudyma.domain.creatures.Creature;

public interface CreatureRepository extends JpaRepository<Creature, Long> {
}
