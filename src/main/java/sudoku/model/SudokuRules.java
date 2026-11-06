package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Sudoku rules for the 6x6 board: checks that a number is not repeated in its
 * row, column or block, finds the cells in conflict, and gives the numbers that
 * can be placed in a cell. It only has static methods and cannot be instantiated.
 */

public final class SudokuRules {

    /** Private on purpose: this class only offers static methods, so no object is needed. */

    private SudokuRules() {
    }

    /** Tells if a number can be placed in a cell without repeating it in its row, column or block.
     * @param board the board to check
     * @param row row of the cell
     * @param column column of the cell
     * @param value number to place (1 to 6)
     * @return true if the move follows the rules
     */

    public static boolean isValidMove(Board board, int row, int column, int value) {
        // A position outside the board is never valid
        if (!sudoku.model.Board.isOnBoard(row, column)) {
            return false;
        }
        // Only numbers from 1 to 6 are allowed
        if (value < sudoku.model.Board.MIN_VALUE || value > sudoku.model.Board.MAX_VALUE) {
            return false;
        }
        // Valid only if the number is not in the row, the column or the block
        return !containsInRow(board, row, column, value)
                && !containsInColumn(board, column, row, value)
                && !containsInBlock(board, row, column, value);
    }

    /** Tells if a number is already in a row.
     * @param board the board to check
     * @param row row to look in
     * @param value number to look for
     * @return true if the number is in the row
     */

    public static boolean containsInRow(Board board, int row, int value) {
        // -1 means no column is skipped: look in the whole row
        return containsInRow(board, row, -1, value);
    }

    /** Tells if a number is already in a column.
     * @param board the board to check
     * @param column column to look in
     * @param value number to look for
     * @return true if the number is in the column
     */

    public static boolean containsInColumn(Board board, int column, int value) {
        // -1 means no row is skipped: look in the whole column
        return containsInColumn(board, column, -1, value);
    }

    /** Tells if a number is already in the block of a cell, ignoring the cell itself.
     * @param board the board to check
     * @param row row of the cell
     * @param column column of the cell
     * @param value number to look for
     * @return true if another cell of the block has the number
     */

    public static boolean containsInBlock(Board board, int row, int column, int value) {
        // First row and first column of the block where the cell is
        int startRow = (row / sudoku.model.Board.ROWS_PER_BLOCK) * sudoku.model.Board.ROWS_PER_BLOCK;
        int startColumn = (column / sudoku.model.Board.COLUMNS_PER_BLOCK) * sudoku.model.Board.COLUMNS_PER_BLOCK;
        // Go through every cell of the block
        for (int f = startRow; f < startRow + sudoku.model.Board.ROWS_PER_BLOCK; f++) {
            for (int c = startColumn; c < startColumn + sudoku.model.Board.COLUMNS_PER_BLOCK; c++) {
                // Skip the cell being checked, so it does not conflict with itself
                if ((f != row || c != column) && board.get(f, c) == value) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds all the cells that repeat a number in their row, column or block.
     *
     * @param board the board to check
     * @return the cells in conflict (empty if the board has no repeated numbers)
     */

    public static Set<Position> conflicts(Board board) {
        // A set does not keep repeated cells, so each conflict appears once
        Set<Position> conflicts = new LinkedHashSet<>();
        // Rows: compare each cell with the ones to its right
        for (int row = 0; row < sudoku.model.Board.SIZE; row++) {
            for (int column = 0; column < sudoku.model.Board.SIZE; column++) {
                int value = board.get(row, column);
                // Empty cells cannot be in conflict
                if (value == sudoku.model.Board.EMPTY) {
                    continue;
                }
                for (int otherColumn = column + 1; otherColumn < sudoku.model.Board.SIZE; otherColumn++) {
                    // Same number twice in the row: both cells are in conflict
                    if (board.get(row, otherColumn) == value) {
                        conflicts.add(new Position(row, column));
                        conflicts.add(new Position(row, otherColumn));
                    }
                }
            }
        }

        for (int column = 0; column < sudoku.model.Board.SIZE; column++) {
            for (int row = 0; row < sudoku.model.Board.SIZE; row++) {
                int value = board.get(row, column);
                if (value == sudoku.model.Board.EMPTY) {
                    continue;
                }
                // Columns: compare each cell with the ones below it
                for (int otherRow = row + 1; otherRow < sudoku.model.Board.SIZE; otherRow++) {
                    if (board.get(otherRow, column) == value) {
                        conflicts.add(new Position(row, column));
                        conflicts.add(new Position(otherRow, column));
                    }
                }
            }
        }
        // Blocks: go through each block of 2x3
        for (int blockRow = 0; blockRow < sudoku.model.Board.SIZE; blockRow += sudoku.model.Board.ROWS_PER_BLOCK) {
            for (int blockColumn = 0; blockColumn < sudoku.model.Board.SIZE; blockColumn += sudoku.model.Board.COLUMNS_PER_BLOCK) {
                // List the 6 cells of this block
                List<Position> cells = new ArrayList<>();
                for (int row = blockRow; row < blockRow + sudoku.model.Board.ROWS_PER_BLOCK; row++) {
                    for (int column = blockColumn; column < blockColumn + sudoku.model.Board.COLUMNS_PER_BLOCK; column++) {
                        cells.add(new Position(row, column));
                    }
                }
                // Compare each cell of the block with the ones after it
                for (int i = 0; i < cells.size(); i++) {
                    Position current = cells.get(i);
                    int value = board.get(current.getRow(), current.getColumn());
                    if (value == sudoku.model.Board.EMPTY) {
                        continue;
                    }
                    for (int j = i + 1; j < cells.size(); j++) {
                        Position other = cells.get(j);
                        if (board.get(other.getRow(), other.getColumn()) == value) {
                            conflicts.add(current);
                            conflicts.add(other);
                        }
                    }
                }
            }
        }

        return conflicts;
    }

    /**
     * Finds the cells that repeat the number of one specific cell, in its row,
     * column or block. The cell itself is included when there is a conflict.
     *
     * @param board the board to check
     * @param row    row of the cell
     * @param column column of the cell
     * @return the cells in conflict with that cell (empty if there is none)
     */

    public static Set<Position> conflictsOf(Board board, int row, int column) {
        int value = board.get(row, column);
        // An empty cell has no conflicts
        if (value == sudoku.model.Board.EMPTY) {
            return Collections.emptySet();
        }

        Set<Position> conflicts = new LinkedHashSet<>();
        // Same row: other cells with the same number
        for (int c = 0; c < sudoku.model.Board.SIZE; c++) {
            if (c != column && board.get(row, c) == value) {
                conflicts.add(new Position(row, c));
            }
        }
        // Same column: other cells with the same number
        for (int f = 0; f < sudoku.model.Board.SIZE; f++) {
            if (f != row && board.get(f, column) == value) {
                conflicts.add(new Position(f, column));
            }
        }
        // Same block: other cells with the same number
        for (int f = 0; f < sudoku.model.Board.SIZE; f++) {
            for (int c = 0; c < sudoku.model.Board.SIZE; c++) {
                if ((f != row || c != column)
                        && sudoku.model.Board.isInSameBlock(row, column, f, c)
                        && board.get(f, c) == value) {
                    conflicts.add(new Position(f, c));
                }
            }
        }
        // No repeated number: nothing to mark
        if (conflicts.isEmpty()) {
            return conflicts;
        }
        // Add the cell itself too, so it can also be highlighted
        conflicts.add(new Position(row, column));
        return conflicts;
    }

    /**
     * Lists the numbers that can be placed in a cell following the rules.
     *
     * @param board the board to check
     * @param row    row of the cell
     * @param column column of the cell
     * @return the possible numbers (empty if there is none or the position is outside the board)
     */

    public static List<Integer> candidates(Board board, int row, int column) {
        List<Integer> candidates = new ArrayList<>();
        // Outside the board: no numbers can be placed
        if (!sudoku.model.Board.isOnBoard(row, column)) {
            return candidates;
        }
        // Try each number from 1 to 6
        for (int value = sudoku.model.Board.MIN_VALUE; value <= sudoku.model.Board.MAX_VALUE; value++) {
            // Keep the numbers that do not repeat in row, column or block
            if (isValidMove(board, row, column, value)) {
                candidates.add(value);
            }
        }
        return candidates;
    }

    /**
     * Tells if the board has no repeated numbers in any row, column or block.
     *
     * @param board the board to check
     * @return true if there are no conflicts
     */

    public static boolean isValidBoard(Board board) {
        // The board is valid when there are no cells in conflict
        return conflicts(board).isEmpty();
    }

    /** Looks for a number in a row, skipping one column.
     * @param board the board to check
     * @param row row to look in
     * @param excludedColumn column to skip (-1 to skip none)
     * @param value number to look for
     * @return true if the number is found
     */

    private static boolean containsInRow(Board board, int row, int excludedColumn, int value) {
        for (int column = 0; column < sudoku.model.Board.SIZE; column++) {
            // Skip the excluded cell and compare the others
            if (column != excludedColumn && board.get(row, column) == value) {

                return true;
            }
        }
        return false;
    }

    /** Looks for a number in a column, skipping one row.
     * @param board the board to check
     * @param column column to look in
     * @param excludedRow row to skip (-1 to skip none)
     * @param value number to look for
     * @return true if the number is found
     */

    private static boolean containsInColumn(Board board, int column, int excludedRow, int value) {
        for (int row = 0; row < sudoku.model.Board.SIZE; row++) {
            // Skip the excluded cell and compare the others
            if (row != excludedRow && board.get(row, column) == value) {
                return true;
            }
        }
        return false;
    }
}
