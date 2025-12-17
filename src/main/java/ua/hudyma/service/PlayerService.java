package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.players.dto.PlayerReqDto;
import ua.hudyma.domain.players.dto.PlayerRespDto;
import ua.hudyma.domain.players.dto.ResourcesReqDto;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.config.AbstractBuildingConfig;
import ua.hudyma.domain.towns.config.CastleBuildingConfig;
import ua.hudyma.domain.towns.enums.HallType;
import ua.hudyma.mapper.PlayerMapper;
import ua.hudyma.repository.PlayerRepository;
import ua.hudyma.resource.enums.ResourceType;

import java.util.List;
import java.util.Map;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;
import static ua.hudyma.util.MessageProcessor.getReturnMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Transactional
    public String addResources(ResourcesReqDto dto) {
        var player = getPlayer(dto.playerId());
        var resourceMap = player.getResourceMap();
        if (resourceMap == null) {
            player.setResourceMap(dto.resourceMap());
        } else {
            dto.resourceMap()
                    .forEach(
                            (key, addValue) -> resourceMap
                                    .merge(key, addValue, Integer::sum)
                    );
        }
        return String.format("%s's resources have been replenished", player.getName());
    }

    @Transactional/*(readOnly = true)*/
    public Integer calcDailyIncome(Long playerId) {
        var player = getPlayer(playerId);
        return player
                .getTownsList()
                .stream()
                .map(Town::getBuildingConfig)
                .filter(CastleBuildingConfig.class::isInstance)
                .map(CastleBuildingConfig.class::cast)
                .mapToInt(cfg -> cfg.getHall().getIncome())
                .sum();
    }

    @SneakyThrows
    public String createPlayer(PlayerReqDto dto) {
        var player = playerMapper.toEntity(dto);
        playerRepository.save(player);
        return getReturnMessage(player, "name");
    }

    @Transactional
    public PlayerRespDto fetchPlayer(Long playerId) {
        return playerMapper.toDto(getPlayer(playerId));
    }

    public Map<ResourceType, Integer> fetchResource(Long playerId) {
        var player = getPlayer(playerId);
        return player.getResourceMap();
    }

    public Player getPlayer(Long playerId) {
        return playerRepository
                .findById(playerId)
                .orElseThrow(getExceptionSupplier(
                        Player.class,
                        playerId,
                        EntityNotFoundException::new));
    }
}
