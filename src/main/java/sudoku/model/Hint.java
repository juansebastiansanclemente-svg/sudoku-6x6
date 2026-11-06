package sudoku.model;

/**
 * Holds a hint given to the player: which cell to fill, which number to put
 * in it, and how many hints are left. It cannot be changed after it is created.
 */

public final class Hint {
    /** Cell where the hint suggests placing a number. */
    private final Position position;
    /** Number suggested for that cell (from 1 to 6). */
    private final int value;
    /** Hints the player still has after using this one. */
    private final int remainingHint;
    /**
     * Creates a hint.
     *
     * @param position        cell where the number is suggested
     * @param value           number suggested for that cell
     * @param reaminingHint hints left after this one
     */
    public Hint(Position position, int value, int reaminingHint) {
        // Save the values, they never change because the fields are final
        this.position = position;
        this.value = value;
        this.remainingHint = reaminingHint;
    }
    /** Returns the cell where the number is suggested.
     * @return the suggested cell */

    public Position getPosition() {
        return position;
    }

    /** Returns the number suggested for the cell.
     * @return the suggested number */

    public int getValue() {
        return value;
    }

    /** Returns how many hints the player has left.
     * @return the remaining hints */

    public int remainingHint() {
        return remainingHint;
    }

    /**
     * Returns the hint as text, for example "Sugerencia: coloca 4 en (2, 3)".
     *
     * @return the text representation of this hint
     */

    @Override
    // Joins the number and the cell into one readable sentence
    public String toString() {
        return "Sugerencia: coloca " + value + " en " + position;
    }
}
