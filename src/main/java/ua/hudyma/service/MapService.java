package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.map.dto.MapCoordinatesDto;
import ua.hudyma.domain.map.dto.TownDataDto;
import ua.hudyma.domain.map.enums.MapSize;
import ua.hudyma.domain.map.enums.TownName;
import ua.hudyma.util.IdGenerator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Log4j2
public class MapService {

    private static final List<TownDataDto> townDataList = new ArrayList<>();
    private static final ThreadLocalRandom randomiser = ThreadLocalRandom.current();
    private static int[][] mapArray = new int[0][];
    private static final int TOWN_SIZE = 5;
    private static int MAP_SIZE_PARAM, col, row;

    public int calcTownQuantity() {
        var townIndexSet = new HashSet<Integer>();
        for (int col = 0; col < mapArray[0].length; col++){
            for (int row = 0; row < mapArray[0].length; row++){
                if (mapArray[col][row] != 0){
                    townIndexSet.add(mapArray[col][row]);
                }
            }
        }
        return townIndexSet.size();
    }

    public List<TownDataDto> randomlyPositionTowns
            (int townQuantity, MapSize mapSize) {
        MAP_SIZE_PARAM = mapSize.getMapSize();
        //in real game, map icon sizes differ, here all sizes are set uniformly for simplicity (5x5)
        mapArray = new int[MAP_SIZE_PARAM][MAP_SIZE_PARAM];
        for (int i = 0; i < townQuantity; i++){
            randomiseCoords();
            var townIndex = getRandomTownIndex();
            generateTownSpaceAndModifyMapArray(townIndex);
            var townName = IdGenerator.getRandomEnum(TownName.class);
            townDataList.add(new TownDataDto(townIndex, townName, new MapCoordinatesDto(col, row)));
        }
        return townDataList;
    }

    private static void randomiseCoords() {
        col = getRandom(MAP_SIZE_PARAM);
        row = getRandom(MAP_SIZE_PARAM);
        if (!areRandomisedCoordsVacant())
            randomiseCoords();
    }

    private static boolean areRandomisedCoordsVacant() {
        for (int c = col - TOWN_SIZE / 2 - 1 ; c < col + TOWN_SIZE / 2; c++){
            for (int r = row - TOWN_SIZE / 2 - 1; r < row + TOWN_SIZE / 2; r++){
                if (mapArray[c][r] != 0){
                   log.error("Position at [{}:{}] occupied by {}",
                           col, row, retrieveTownNameByIndex(mapArray[c][r]));
                   return false;
                }
            }
        }
        return true;
    }

    private static TownName retrieveTownNameByIndex(int townIndex) {
        return townDataList
                .stream()
                .filter(townDataDto -> townDataDto
                        .townIndex() == townIndex)
                .map(TownDataDto::townName)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException
                        ("No town retrieved by index = " + townIndex));
    }

    private int getRandomTownIndex() {
        var random = randomiser.nextInt(TownName.values().length) + 1;
        return tomnIndexAlreadyExists(random) ? getRandomTownIndex() : random;
    }

    private boolean tomnIndexAlreadyExists(int random) {
        return townDataList
                .stream()
                .map(TownDataDto::townIndex)
                .anyMatch(index -> index == random);
    }

    private static int getRandom(int mapSizeParam) {
        int random = randomiser.nextInt(mapSizeParam) + 1;
        return random < TOWN_SIZE / 2 || random + TOWN_SIZE / 2 > mapSizeParam
                ? getRandom(mapSizeParam) :
                random;
    }

    private void generateTownSpaceAndModifyMapArray(int townIndex) {
        for (int c = col - TOWN_SIZE / 2 - 1 ; c < col + TOWN_SIZE / 2; c++){
            for (int r = row - TOWN_SIZE / 2 - 1; r < row + TOWN_SIZE / 2; r++){
                mapArray[c][r] = townIndex;
            }
        }
    }

    public TownName getNearestTown(int heroCol, int heroRow) {
        TownName nearestTown = TownName._NO_CITY_;
        int minimalDistance;
        if (townDataList.isEmpty())
            throw new IllegalArgumentException("No towns allocated, townMap is Empty");
        else if (townDataList.size() == 1) {
            return townDataList.get(0).townName();
        }
        else {
            minimalDistance = MAP_SIZE_PARAM;
            for (TownDataDto townData : townDataList){
                var mapCoordinatesDto = townData.mapCoordinatesDto();
                var townCol = mapCoordinatesDto.col();
                var townRow = mapCoordinatesDto.row();
                var distance = calculateDistance(heroCol, heroRow, townCol, townRow);
                if (distance < minimalDistance){
                    minimalDistance = distance;
                    nearestTown = townData.townName();
                }
            }
        }
        log.info(".....Nearest distance = " + minimalDistance);
        return nearestTown;
    }

    private int calculateDistance(int heroCol, int heroRow, int townCol, int townRow) {
        return (int) Math.sqrt(Math.pow(heroCol + townCol, 2d) + Math.pow(heroRow + townRow, 2d));
    }
}
