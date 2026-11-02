package sudoku.model;

import java.util.Objects;

/**
 * Represents the position on the Sudoku board, given by the row and column.
 * It cannot be changed after it is created.
 */

public final class Posicion {
    /** Row of the cell, counted from 0 at the top. */
    private final int fila;
    /** Row of the cell, counted from 0 at the top. */
    private final int columna;

    /** Creates a position with the given row and column.
     * @param fila the row index of the cell
     * @param columna the column index of the cell */

    public Posicion(int fila, int columna) {
        // Save the values; they never change because the fields are final
        this.fila = fila;
        this.columna = columna;
    }
    /** Returns the row index of this position.
     * @return the row index */

    public int getFila() {
        return fila;
    }

    /** Returns the column index of this position.
     * @return the column index */

    public int getColumna() {
        return columna;
    }

    /** Compares this position with another object; they are equal when row and column match.
     * @param otro the object to compare with
     * @return true if both positions have the same row and column */

    @Override
    public boolean equals(Object otro) {
        // Same object: it is equal to itself
        if (this == otro) {
            return true;
        }
        // Not a Posicion (or null): it cannot be equal
        if (!(otro instanceof Posicion)) {
            return false;
        }
        // Safe to convert, because the check above passed
        Posicion posicion = (Posicion) otro;
        // Equal only when row and column both match
        return fila == posicion.fila && columna == posicion.columna;
    }
    /** Returns a hash code consistent with {@link #equals(Object)}.
     * @return the hash code of this position */

    @Override
    // Uses the same fields as equals, so equal positions give the same hash.
    // Needed because positions are stored in sets (for example, the conflicts).
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    /** Returns the position as text in the form "(row, column)".
     * @return the text representation of this position */

    @Override
    // Example: row 2 and column 4 give "(2, 4)"
    public String toString() {
        return "(" + fila + ", " + columna + ")";
    }
}
