package sudoku.model;

/**
 * Contract for any class that creates the starting board of a game.
 * Different generators can be used without changing the game class,
 * because the game only depends on this interface.
 */

public interface GeneradorTablero {

    /**
     * Creates a new board with its initial clues already placed and fixed.
     *
     * @return a new board ready to play
     */

    Tablero generar();
}
