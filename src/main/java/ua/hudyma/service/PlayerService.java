package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.HeroSpecialty;
import ua.hudyma.domain.heroes.enums.HeroFaction;
import ua.hudyma.domain.heroes.enums.HeroSpecialtyType;
import ua.hudyma.domain.heroes.enums.SecondarySkill;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.players.dto.PlayerReqDto;
import ua.hudyma.domain.players.dto.PlayerRespDto;
import ua.hudyma.domain.players.dto.ResourcesReqDto;
import ua.hudyma.domain.players.enums.PlayerColour;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.domain.towns.enums.HallType;
import ua.hudyma.enums.WarMachine;
import ua.hudyma.mapper.PlayerMapper;
import ua.hudyma.repository.PlayerRepository;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.util.IdGenerator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static ua.hudyma.domain.towns.enums.UniqueBuildingType.TREASURY;
import static ua.hudyma.enums.Faction.RAMPART;
import static ua.hudyma.resource.enums.ResourceType.GOLD;
import static ua.hudyma.util.IdGenerator.getRandomEnum;
import static ua.hudyma.util.IdGenerator.getThreadLocalRandomIndex;
import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;
import static ua.hudyma.util.MessageProcessor.getReturnMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlayerService {

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    //private final ArmyService armyService; incurs circular via HeroService
    //private final HeroService heroService; incurs circular
    private final SpellService spellService;
    private final CreatureService creatureService;
    private final RandomService randomService;

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

    @Transactional(readOnly = true)
    public Integer calcDailyIncome(Long playerId) {
        var player = getPlayer(playerId);
        return player
                .getTownsList()
                .stream()
                .map(Town::getHallType)
                .filter(Objects::nonNull)
                .mapToInt(HallType::getIncome)
                .sum();
    }

    @Transactional(readOnly = true)
    public Integer calcDailyIncome(Player player) {
        return player
                .getTownsList()
                .stream()
                .map(Town::getHallType)
                .filter(Objects::nonNull)
                .mapToInt(HallType::getIncome)
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
                        EntityNotFoundException::new, false));
    }


    /**
     * On the first day of the week, it produces extra gold equal to 10%
     * of the player's total gold they had on the seventh day of the last
     * week (the day prior to generating this extra income)
     */
    @Transactional
    public String calculateTreasuriesWeeklyInterestIncomeIfAny() {
        //need to check if it's 7th day of the week
        var playerList = playerRepository.findAll();
        for (Player player : playerList) {
            Map<ResourceType, Integer> resourceMap = null;
            var goldWithIncome = 0;
            var playerGold = 0;
            var townList = player.getTownsList();
            var rampartTownsList = townList
                    .stream()
                    .filter(town -> town.getFaction() == RAMPART)
                    .toList();
            if (rampartTownsList.isEmpty()) {
                log.error("No Rampart towns have been found");
                continue;
            }
            resourceMap = player.getResourceMap();
            for (Town town : rampartTownsList) {
                if (town.getUniqueBuildingSet().contains(TREASURY.name())) {
                    var interestRate = TREASURY.getValue();
                    playerGold = player.getResourceMap().get(GOLD);
                    var dailyIncome = calcDailyIncome(player);
                    goldWithIncome = playerGold + dailyIncome;
                    int interestTotal = goldWithIncome
                            * interestRate / 100;
                    goldWithIncome += interestTotal;
                }
            }
            if (goldWithIncome > playerGold) {
                resourceMap.put(GOLD, goldWithIncome);
                return "Income for " + player.getName() + " has been increased by "
                        + goldWithIncome;
            }
        }

        return "No rampart towns with treasuries have been found";
    }


    




}
