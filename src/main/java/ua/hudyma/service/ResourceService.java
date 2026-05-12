package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.players.Player;
import ua.hudyma.resource.enums.MineType;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ResourceService {

    private final PlayerService playerService;

    public Map<MineType, Integer> getMines(Long playerId) {
        var player = playerService.getPlayer(playerId);
        return player.getMinesMap();
    }

    @Transactional
    public String addMine(MineType mineType, Long playerId) {
        var player = playerService.getPlayer(playerId);
        var mineMap = fetchOrCreateMineMap(player);
        mineMap.merge(mineType, 1, Integer::sum);
        player.setMinesMap(mineMap);
        return mineType + " succ acquired by " + player.getName();
    }

    private static Map<MineType, Integer> fetchOrCreateMineMap(Player player) {
        return player.getMinesMap() == null ?
                new EnumMap<>(MineType.class) :
                player.getMinesMap();
    }

    public Map<ResourceType, Integer> getMinesWeeklyIncome(Long playerId) {
        var player = playerService.getPlayer(playerId);
        var mineMap = player.getMinesMap();
        if (mineMap == null || mineMap.isEmpty()) {
            throw new IllegalStateException("Mine Map is empty, no income");
        }
        var incomeMap = new EnumMap<ResourceType, Integer>(ResourceType.class);
        for (Map.Entry<MineType, Integer> entry : mineMap.entrySet()) {
            var mineType = entry.getKey();
            var mineQty = entry.getValue();
            var resourceType = mineType.getResourceType();
            var resourceRateIncome = mineType.getWeeklyProductionRate();
            incomeMap.put(resourceType, mineQty * resourceRateIncome);
        }
        return incomeMap;
    }


}
