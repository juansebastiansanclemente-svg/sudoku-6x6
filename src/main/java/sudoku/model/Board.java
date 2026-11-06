package sudoku.model;

import java.util.Arrays;

/**
 * The 6x6 Sudoku board. It stores the number of each cell and which cells are
 * fixed (the initial clues that the player cannot change). The board is split
 * into 6 blocks of 2 rows by 3 columns. Rows and columns start at 0.
 */

public class Board {

    /** Number of rows and columns of the board. */
    public static final int SIZE = 6;
    /** Rows in each block. */
    public static final int ROWS_PER_BLOCK = 2;
    /** Columns in each block. */
    public static final int COLUMNS_PER_BLOCK = 3;
    /** Smallest number a cell can hold. */
    public static final int MIN_VALUE = 1;
    /** Biggest number a cell can hold. */
    public static final int MAX_VALUE = 6;
    /** Value that means the cell is empty. */
    public static final int EMPTY = 0;

    /** Number in each cell (0 if empty). */
    private final int[][] cells;
    /** True in the cells that are initial clues and cannot be changed. */
    private final boolean[][] fixedCells;

    /** Creates an empty board with no fixed cells. */
    public Board() {
        // All numbers start at 0 (empty)
        cells = new int[SIZE][SIZE];
        // All cells start as not fixed
        fixedCells = new boolean[SIZE][SIZE];
    }

    /** Returns the number in a cell.
     * @param row row of the cell
     * @param column column of the cell
     * @return the number (0 if empty)
     * @throws IllegalArgumentException if the position is outside the board
     */

    public int get(int row, int column) {
        // Stop here if the position is outside the board
        validatePosition(row, column);
        return cells[row][column];
    }

    /** Puts a number in a cell. Use 0 to empty it.
     * @param row row of the cell
     * @param column column of the cell @param valor number from 1 to 6, or 0 to empty the cell
     * @throws IllegalArgumentException if the position or the number is not valid
     * @throws IllegalStateException if the cell is fixed and the number is different
     */

    public void set(int row, int column, int valor) {
        // Stop here if the position is outside the board
        validatePosition(row, column);
        // Stop here if the number is not between 0 and 6
        validateValue(valor);
        // A fixed cell can only be "set" to its own number
        if (fixedCells[row][column] && cells[row][column] != valor) {
            throw new IllegalStateException("No se puede modificar una celda fija.");
        }
        cells[row][column] = valor;
    }

    /** Puts a number in a cell and marks it as fixed (an initial clue).
     * @param row row of the cell
     * @param column column of the cell
     * @param value number from 1 to 6
     * @throws IllegalArgumentException if the position or the number is not valid
     */

    public void fix(int row, int column, int value) {
        // Stop here if the position is outside the board
        validatePosition(row, column);
        // Fixed clues must be a real number (1 to 6), never empty
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("El value debe estar entre 1 y 6.");
        }
        cells[row][column] = value;
        // Mark it as a clue the player cannot change
        fixedCells[row][column] = true;
    }

    /** Tells if a cell is a fixed clue.
     * @param row row of the cell
     * @param column column of the cell
     * @return true if the cell is fixed
     */

    public boolean isFixed(int row, int column) {
        // Stop here if the position is outside the board
        validatePosition(row, column);
        return fixedCells[row][column];
    }

    /** Tells if a cell is empty.
     * @param row row of the cell
     * @param column column of the cell
     * @return true if the cell has no number
     */

    public boolean isEmpty(int row, int column) {
        // Stop here if the position is outside the board
        validatePosition(row, column);
        return cells[row][column] == EMPTY;
    }

    /** Tells if every cell has a number.
     * @return true if there are no empty cells
     */

    public boolean isFull() {
        // Go through every cell
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (cells[row][column] == EMPTY) {
                    // One empty cell is enough to know the board is not full
                    return false;
                }
            }
        }
        // No empty cell was found
        return true;
    }

    /** Counts the fixed clues.
     * @return number of fixed cells
     */

    public int countFixedCells() {
        // Counter that grows each time a matching cell is found
        int total = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (fixedCells[row][column]) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Counts the empty cells.
     * @return number of empty cells
     */

    public int countEmptyCells() {
        // Counter that grows each time a matching cell is found
        int total = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (cells[row][column] == EMPTY) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Empties the whole board and removes all fixed clues.
     */

    public void clear() {
        for (int row = 0; row < SIZE; row++) {
            // Fill the row with 0 (empty) and not fixed
            Arrays.fill(cells[row], EMPTY);
            Arrays.fill(fixedCells[row], false);
        }
    }

    /** Empties only the cells filled by the player; the fixed clues stay.
     */

    public void clearPlayerMoves() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                // Only empty the cells that are not fixed clues
                if (!fixedCells[row][column]) {
                    cells[row][column] = EMPTY;
                }
            }
        }
    }

    /** Creates an independent copy of the board, including the fixed cells.
     * @return a new board with the same content
     */

    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < SIZE; row++) {
            // Copy the row of numbers and the row of fixed marks
            System.arraycopy(cells[row], 0, copy.cells[row], 0, SIZE);
            System.arraycopy(fixedCells[row], 0, copy.fixedCells[row], 0, SIZE);
        }
        // The copy does not share data with the original, so changing one does not change the other
        return copy;
    }

    /** Copies only the numbers of the cells.
     * @return a new 6x6 array with the numbers
     */

    public int[][] copyCells() {
        int[][] copy = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            // Copy row by row, so the new array is independent
            System.arraycopy(cells[row], 0, copy[row], 0, SIZE);
        }
        return copy;
    }

    /** Tells if a position is inside the board.
     * @param row row to check
     * @param column column to check
     * @return true if both are between 0 and 5
     */

    public static boolean isOnBoard(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    /** Tells if two cells are in the same block of 2x3.
     * @param row1 row of the first cell
     * @param column1 column of the first cell
     * @param row2 row of the second cell
     * @param column2 column of the second cell
     * @return true if both cells are in the same block
     */

    public static boolean isInSameBlock(int row1, int column1, int row2, int column2) {
        // Dividing by the block size gives the block row and block column; both must match
        return row1 / ROWS_PER_BLOCK == row2 / ROWS_PER_BLOCK
                && column1 / COLUMNS_PER_BLOCK == column2 / COLUMNS_PER_BLOCK;
    }

    /** Gives the number of the block of a cell. Blocks go from 0 to 5, left to right and top to bottom.
     * @param row row of the cell
     * @param column column of the cell
     * @return the block number
     */

    public static int getBlockNumber(int row, int column) {
        // How many blocks fit side by side in one row of the board (6 / 3 = 2 (6 blocks, 3 columns, 2 rows))
        int blocksPerRow = SIZE / COLUMNS_PER_BLOCK;
        // Skip the full block rows above, then add the block column
        return (row / ROWS_PER_BLOCK) * blocksPerRow + (column / COLUMNS_PER_BLOCK);
    }

    /** Throws an error if the position is outside the board.
     * @param row row to check
     * @param column column to check
     * @throws IllegalArgumentException if the position is outside the board
     */

    private void validatePosition(int row, int column) {
        if (!isOnBoard(row, column)) {
            throw new IllegalArgumentException("Posición fuera del tablero: (" + row + ", " + column + ")");
        }
    }

    /** Throws an error if the number is not between 0 and 6.
     * @param value number to check
     * @throws IllegalArgumentException if the number is out of range
     */

    private void validateValue(int value) {
        if (value < EMPTY || value > MAX_VALUE) {
            throw new IllegalArgumentException("El valor debe estar entre 0 y 6.");
        }
    }

    /**
     * Draws the board as text, with lines that separate the blocks. Empty cells are shown as ".".
     *
     * @return the board as text
     */

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            // Draw a line between blocks of rows
            if (row > 0 && row % ROWS_PER_BLOCK == 0) {
                texto.append("------+------\n");
            }
            for (int column = 0; column < SIZE; column++) {
                // Draw a bar between blocks of columns
                if (column > 0 && column % COLUMNS_PER_BLOCK == 0) {
                    texto.append("| ");
                }
                // Show "." for empty cells and the number for the others
                texto.append(cells[row][column] == EMPTY ? "." : cells[row][column]).append(' ');
            }
            texto.append('\n');
        }
        return texto.toString();
    }
}
