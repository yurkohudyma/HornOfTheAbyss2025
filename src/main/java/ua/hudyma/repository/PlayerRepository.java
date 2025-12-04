package ua.hudyma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.hudyma.domain.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
