package ua.hudyma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.hudyma.domain.players.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
