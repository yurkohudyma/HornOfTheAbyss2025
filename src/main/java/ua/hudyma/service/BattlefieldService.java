package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.battlefield.Hex;

@Service
@RequiredArgsConstructor
@Log4j2
public class BattlefieldService {

    private final int cols = 11, rows = 15;
    private final Hex[][] grid = new Hex[cols][rows];

    public Hex[][] initBattlefieldMap() {
        createHexes();
        assignNeighbors();
        return grid;
    }

    private void createHexes() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                grid[col][row] = new Hex();
            }
        }
    }

    private void assignNeighbors() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                var hex = grid[col][row];
                boolean oddRow = row % 2 == 1;
                hex.setW(get(col - 1, row));
                hex.setE(get(col + 1, row));
                if (oddRow) {
                    hex.setNw(get(col, row - 1));
                    hex.setNe(get(col + 1, row - 1));
                    hex.setSw(get(col, row + 1));
                    hex.setSe(get(col + 1, row + 1));
                } else {
                    hex.setNw(get(col - 1, row - 1));
                    hex.setNe(get(col, row - 1));
                    hex.setSw(get(col - 1, row + 1));
                    hex.setSe(get(col, row + 1));
                }
            }
        }
    }

    private Hex get(int col, int row) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) {
            return null;
        }
        return grid[col][row];
    }

    public void renderBattlefield() {
        for (int col = 0; col < cols; col++) {
            String indent = (col % 2 == 0) ? "   " : "";
            System.out.print(indent);
            for (int row = 0; row < rows; row++) {
                System.out.print(" /‾‾\\");
            }
            System.out.println();
            System.out.print(indent);
            for (int row = 0; row < rows; row++) {
                char symbol = ' '; // або getCellSymbol(col, row)
                System.out.print("| " + symbol + " |");
            }
            System.out.println();
            System.out.print(indent);
            for (int row = 0; row < rows; row++) {
                System.out.print(" \\__/");
            }
            System.out.println();
        }
    }
}