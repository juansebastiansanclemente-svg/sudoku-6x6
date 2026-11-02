package sudoku.model;

/**
 * Holds a hint given to the player: which cell to fill, which number to put
 * in it, and how many hints are left. It cannot be changed after it is created.
 */

public final class Sugerencia {
    /** Cell where the hint suggests placing a number. */
    private final Posicion posicion;
    /** Number suggested for that cell (from 1 to 6). */
    private final int valor;
    /** Hints the player still has after using this one. */
    private final int ayudasRestantes;
    /**
     * Creates a hint.
     *
     * @param posicion        cell where the number is suggested
     * @param valor           number suggested for that cell
     * @param ayudasRestantes hints left after this one
     */
    public Sugerencia(Posicion posicion, int valor, int ayudasRestantes) {
        // Save the values, they never change because the fields are final
        this.posicion = posicion;
        this.valor = valor;
        this.ayudasRestantes = ayudasRestantes;
    }
    /** Returns the cell where the number is suggested.
     * @return the suggested cell */

    public Posicion getPosicion() {
        return posicion;
    }

    /** Returns the number suggested for the cell.
     * @return the suggested number */

    public int getValor() {
        return valor;
    }

    /** Returns how many hints the player has left.
     * @return the remaining hints */

    public int getAyudasRestantes() {
        return ayudasRestantes;
    }

    /**
     * Returns the hint as text, for example "Sugerencia: coloca 4 en (2, 3)".
     *
     * @return the text representation of this hint
     */

    @Override
    // Joins the number and the cell into one readable sentence
    public String toString() {
        return "Sugerencia: coloca " + valor + " en " + posicion;
    }
}
