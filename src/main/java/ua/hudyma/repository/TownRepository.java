package ua.hudyma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.hudyma.domain.towns.Town;

public interface TownRepository extends JpaRepository<Town, Long> {
}
