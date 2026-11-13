package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.map.dto.MapCoordinatesDto;
import ua.hudyma.domain.map.dto.TownDataDto;
import ua.hudyma.domain.map.enums.MapSize;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Log4j2
public class MapService {

    private static final List<TownDataDto> townDataList = new ArrayList<>();
    private static final ThreadLocalRandom randomiser = ThreadLocalRandom.current();
    private static byte[][] mapArray = new byte[0][];
    private static final int TOWN_SIZE = 5;

    public Map<String, MapCoordinatesDto> randomlyPositionTowns
            (int townQuantity, MapSize mapSize) {
        int mapSizeParam = mapSize.getMapSize();
        //in real game, map icon sizes differ, here all sizes are set uniformly for simplicity (5x5)
        mapArray = new byte[mapSizeParam][mapSizeParam];
        for (int i = 0; i < townQuantity; i++){
            //todo randomise towncenter
            //todo check randomised town center for vacancy
            //todo populate map space for selected town center
            //todo insert town data into townPositionMap

            var col = getRandom(mapSizeParam);
            var row = getRandom(mapSizeParam);
            //todo put town space coords into mapArray
            var townIndex = getRandomTownIndex();
            generateTownSpaceAndModifyMapArray(col, row, townIndex);
        }
        return Map.of();
    }

    private byte getRandomTownIndex() {
        byte random = (byte) randomiser.nextInt(255);
        return tomnIndexAlreadyExists(random) ? getRandomTownIndex() : random;
    }

    private boolean tomnIndexAlreadyExists(int random) {
        return townDataList.stream().map(TownDataDto::townIndex).anyMatch(index -> index == random);
    }

    private static int getRandom(int mapSizeParam) {
        int random = randomiser.nextInt(mapSizeParam);
        return random < TOWN_SIZE || random + TOWN_SIZE > mapSizeParam
                ? getRandom(mapSizeParam) :
                random;
    }

    private void generateTownSpaceAndModifyMapArray(int col, int row, byte townIndex) {
        for (int c = col - TOWN_SIZE ; c < col + TOWN_SIZE; c++){
            for (int r = row - TOWN_SIZE; r < row + TOWN_SIZE; r++){
                mapArray[c][r] = townIndex;
            }
        }
    }

    public String getNearestTown(MapCoordinatesDto heroPositionDto) {
        if (townDataList.isEmpty())
            throw new IllegalArgumentException("No towns allocated, townMap is Empty");
        else if (townDataList.size() == 1) {
            return townDataList.get(0).townName();
        }
        else {
            //todo calculate distances
        }
        return "";
    }
}
