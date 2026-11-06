package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates a 6x6 Sudoku board. It first builds a complete valid solution with
 * backtracking and then keeps only two clues per 2x3 block. Because the clues
 * are taken from a complete solution, the generated board is always solvable.
 */
public class SudokuGenerator implements BoardGenerator {

    /** Number of visible clues left in each 2x3 block. */
    public static final int CLUES_PER_BLOCK = 2;

    /** Source of randomness used for the solution and the clues. */
    private final Random random;

    /** Creates a generator with a new random source. */
    public SudokuGenerator() {
        this(new Random());
    }

    /** Creates a generator with the given random source.
     * @param random source of randomness
     * @throws IllegalArgumentException if the random source is null
     */
    public SudokuGenerator(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("El generador aleatorio no puede ser nulo.");
        }
        this.random = random;
    }

    /**
     * Builds a complete solution and hides all cells except two per block.
     *
     * @return a new board with 12 fixed clues
     */
    @Override
    public Board generate() {
        Board solution = generateSolution();
        Board board = new Board();

        for (Position position : choosePositions()) {
            board.markFixed(position.getRow(), position.getColumn(),
                    solution.get(position.getRow(), position.getColumn()));
        }
        return board;
    }

    /**
     * Fills the whole board with a valid solution using backtracking.
     *
     * @return a complete board with a valid solution
     */
    private Board generateSolution() {
        Board solution = new Board();
        if (!solve(solution, 0)) {
            throw new IllegalStateException("No fue posible generar una solución completa.");
        }
        return solution;
    }

    /**
     * Tries random numbers in the next cell and backtracks when a number fails.
     *
     * @param board the board being filled
     * @param index position of the cell to fill, from 0 to 35 (row by row)
     * @return true if the rest of the board could be filled
     */
    private boolean solve(Board board, int index) {
        if (index == Board.SIZE * Board.SIZE) {
            return true;
        }

        int row = index / Board.SIZE;
        int column = index % Board.SIZE;
        List<Integer> values = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        Collections.shuffle(values, random);

        for (int value : values) {
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

    /**
     * Chooses two random cells from each 2x3 block to show as clues.
     *
     * @return the 12 chosen positions
     */
    private List<Position> choosePositions() {
        List<Position> positions = new ArrayList<>();
        for (int blockRow = 0; blockRow < Board.SIZE; blockRow += Board.BLOCK_ROWS) {
            for (int blockColumn = 0; blockColumn < Board.SIZE; blockColumn += Board.BLOCK_COLUMNS) {
                List<Position> blockCells = new ArrayList<>();
                for (int row = blockRow; row < blockRow + Board.BLOCK_ROWS; row++) {
                    for (int column = blockColumn; column < blockColumn + Board.BLOCK_COLUMNS; column++) {
                        blockCells.add(new Position(row, column));
                    }
                }
                Collections.shuffle(blockCells, random);
                positions.addAll(blockCells.subList(0, CLUES_PER_BLOCK));
            }
        }
        return positions;
    }
}
