package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SudokuGeneratorTest {

    private static final int BOARDS_VERIFIED = 2000;

    @Test
    void generatesTwelveCluesTwoPerBlock() {
        BoardGenerator generator = new SudokuGenerator(new Random(12345));

        for (int attempt = 0; attempt < 100; attempt++) {
            Board board = generator.generate();

            assertEquals(12, board.countFixed());
            assertEquals(24, board.countEmpty());
            assertTrue(SudokuRules.isValidBoard(board));

            int[] cluesPerBlock = new int[6];
            for (int row = 0; row < Board.SIZE; row++) {
                for (int column = 0; column < Board.SIZE; column++) {
                    if (board.isFixed(row, column)) {
                        cluesPerBlock[Board.getBlockNumber(row, column)]++;
                        int value = board.get(row, column);
                        assertTrue(value >= Board.MIN_VALUE && value <= Board.MAX_VALUE);
                    }
                }
            }
            for (int clues : cluesPerBlock) {
                assertEquals(2, clues);
            }
        }
    }

    @Test
    void allGeneratedBoardsCanBeSolved() {
        BoardGenerator generator = new SudokuGenerator(new Random(2024));

        for (int attempt = 0; attempt < BOARDS_VERIFIED; attempt++) {
            Board board = generator.generate();
            Board solution = board.copy();

            assertTrue(solve(solution, 0), "Tablero sin solución:\n" + board);
            assertTrue(solution.isFull());
            assertTrue(SudokuRules.isValidBoard(solution));

            for (int row = 0; row < Board.SIZE; row++) {
                for (int column = 0; column < Board.SIZE; column++) {
                    if (board.isFixed(row, column)) {
                        assertEquals(board.get(row, column), solution.get(row, column));
                    }
                }
            }
        }
    }

    @Test
    void generatesDifferentBoards() {
        BoardGenerator generator = new SudokuGenerator(new Random(99));
        Set<String> boards = new HashSet<>();

        for (int attempt = 0; attempt < 20; attempt++) {
            boards.add(Arrays.deepToString(generator.generate().copyCells()));
        }

        assertTrue(boards.size() > 1);
    }

    @Test
    void requiresRandomSource() {
        assertThrows(IllegalArgumentException.class, () -> new SudokuGenerator(null));
    }

    @Test
    void implementsBoardGeneratorInterface() {
        BoardGenerator generator = new SudokuGenerator(new Random(1));

        assertNotNull(generator.generate());
    }

    private boolean solve(Board board, int index) {
        if (index == Board.SIZE * Board.SIZE) {
            return true;
        }

        int row = index / Board.SIZE;
        int column = index % Board.SIZE;
        if (!board.isEmpty(row, column)) {
            return solve(board, index + 1);
        }

        for (int value = Board.MIN_VALUE; value <= Board.MAX_VALUE; value++) {
            if (SudokuRules.isValidMove(board, row, column, value)) {
                board.set(row, column, value);
                if (solve(board, index + 1)) {
                    return true;
                }
                board.set(row, column, Board.EMPTY);
            }
        }
        return false;
    }
}
