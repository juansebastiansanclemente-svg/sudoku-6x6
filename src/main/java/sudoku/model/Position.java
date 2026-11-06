package sudoku.model;

import java.util.Objects;

/**
 * Represents the position on the Sudoku board, given by the row and column.
 * It cannot be changed after it is created.
 */

public final class Position {
    /** Row of the cell, counted from 0 at the top. */
    private final int row;
    /** Column of the cell, counted from 0 on the left. */
    private final int column;

    /** Creates a position with the given row and column.
     * @param row the row index of the cell
     * @param column the column index of the cell */

    public Position(int row, int column) {
        // Save the values; they never change because the fields are final
        this.row = row;
        this.column = column;
    }

    /** Returns the row index of this position.
     * @return the row index */

    public int getRow() {
        return row;
    }

    /** Returns the column index of this position.
     * @return the column index */

    public int getColumn() {
        return column;
    }

    /** Compares this position with another object; they are equal when row and column match.
     * @param other the object to compare with
     * @return true if both positions have the same row and column */

    @Override
    public boolean equals(Object other) {
        // Same object: it is equal to itself
        if (this == other) {
            return true;
        }
        // Not a Position (or null): it cannot be equal
        if (!(other instanceof Position)) {
            return false;
        }
        // Safe to convert, because the check above passed
        Position position = (Position) other;
        // Equal only when row and column both match
        return row == position.row && column == position.column;
    }

    /** Returns a hash code consistent with {@link #equals(Object)}.
     * @return the hash code of this position */

    @Override
    // Uses the same fields as equals, so equal positions give the same hash.
    // Needed because positions are stored in sets (for example, the conflicts).
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /** Returns the position as text in the form "(row, column)".
     * @return the text representation of this position */

    @Override
    // Example: row 2 and column 4 give "(2, 4)"
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
