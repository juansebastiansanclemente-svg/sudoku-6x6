package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A Sudoku game. It keeps the board and the hints left, and it is the class the
 * controller talks to: it receives the moves of the player, checks them with the
 * Sudoku rules, and gives hints. The first board comes from a generator.
 */

public class Partida {

    /** Number of hints the player can use in each game. */
    public static final int AYUDAS_POR_PARTIDA = 3;

    /** Creates the board of each new game. */
    private final GeneradorTablero generador;
    /** Used to choose the cell and the number of a hint at random. */
    private final Random random;
    /** Board of the current game. */
    private Tablero tablero;
    /** Hints the player can still use in the current game. */
    private int ayudasRestantes;

    /** Creates a game with the default generator and a random choice. */
    public Partida() {
        // Use the default generator
        this(new GeneradorSudoku());
    }

    /** Creates a game with the given generator.
     * @param generador creates the board of each game
     */

    public Partida(GeneradorTablero generador) {
        // Use a new random source
        this(generador, new Random());
    }

    /** Creates a game with the given generator and random source. It is useful for tests, because a fixed random gives repeatable results.
     * @param generador creates the board of each game
     * @param random chooses the hints at random
     * @throws IllegalArgumentException if any of them is null
     */

    public Partida(GeneradorTablero generador, Random random) {
        // Both are needed to play, so null is not accepted
        if (generador == null || random == null) {
            throw new IllegalArgumentException("El generador y el aleatorio no pueden ser nulos.");
        }
        this.generador = generador;
        this.random = random;
        // Create the first board and give the hints
        iniciarPartida();
    }

    /** Creates a game that starts with a board that is already made (for example, in tests).
     * @param tableroInicial the board to start with
     * @throws IllegalArgumentException if the board is null
     */

    public Partida(Tablero tableroInicial) {
        // Without a board there is no game
        if (tableroInicial == null) {
            throw new IllegalArgumentException("El tablero inicial no puede ser nulo.");
        }
        // Default generator, used only if a new game is requested later
        this.generador = new GeneradorSudoku();
        this.random = new Random();
        // Play on the board that was given
        this.tablero = tableroInicial;
        this.ayudasRestantes = AYUDAS_POR_PARTIDA;
    }

    /** Starts a new game: creates a new board and gives all the hints again.
     * @return the new board
     */

    public Tablero nuevaPartida() {
        // Replace the board and reset the hints
        iniciarPartida();
        return tablero;
    }

    /** Returns the board of the current game.
     * @return the board
     */

    public Tablero getTablero() {
        return tablero;
    }

    /** Returns how many hints the player can still use.
     * @return the remaining hints
     */

    public int getAyudasRestantes() {
        return ayudasRestantes;
    }

    /**
     * Puts a number in a cell and checks it. If the number repeats in its row,
     * column or block, it is still placed, but the cells in conflict are returned
     * so they can be highlighted.
     *
     * @param fila    row of the cell
     * @param columna column of the cell
     * @param valor   number to put (1 to 6)
     * @return the result of the move, with a message for the player
     */

    public ResultadoMovimiento ingresar(int fila, int columna, int valor) {
        // Reject a cell outside the board
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return ResultadoMovimiento.rechazado("La posición está fuera del tablero.");
        }
        // Reject numbers that are not from 1 to 6
        if (valor < Tablero.VALOR_MINIMO || valor > Tablero.VALOR_MAXIMO) {
            return ResultadoMovimiento.rechazado("Solo se permiten números del 1 al 6.");
        }
        // Reject changes to the initial clues
        if (tablero.esFija(fila, columna)) {
            return ResultadoMovimiento.rechazado("Esa celda es una pista fija y no se puede modificar.");
        }
        // The move is allowed: place the number
        tablero.set(fila, columna, valor);
        // Look for cells that repeat this number
        Set<Posicion> conflictos = ReglasSudoku.conflictosDe(tablero, fila, columna);
        // No conflicts: the number follows the rules
        if (conflictos.isEmpty()) {
            // This was the last number: the game is won
            if (estaResuelto()) {
                // Solved = no empty cells and no repeated numbers
                return ResultadoMovimiento.aplicadoValido("¡Tablero completado correctamente!");
            }
            return ResultadoMovimiento.aplicadoValido("Número colocado.");
        }
        // Placed, but it repeats a number: send the cells to highlight
        return ResultadoMovimiento.aplicadoConConflictos(conflictos,
                "El número " + valor + " incumple las reglas del Sudoku.");
    }

    /**
     * Empties a cell that the player filled before.
     *
     * @param fila    row of the cell
     * @param columna column of the cell
     * @return the result of the move, with a message for the player
     */

    public ResultadoMovimiento borrar(int fila, int columna) {
        // Reject a cell outside the board
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return ResultadoMovimiento.rechazado("La posición está fuera del tablero.");
        }
        // Reject changes to the initial clues
        if (tablero.esFija(fila, columna)) {
            return ResultadoMovimiento.rechazado("Esa celda es una pista fija y no se puede modificar.");
        }
        // Nothing to delete in an empty cell
        if (tablero.estaVacia(fila, columna)) {
            return ResultadoMovimiento.rechazado("La celda ya está vacía.");
        }
        // Empty the cell
        tablero.set(fila, columna, Tablero.VACIA);
        return ResultadoMovimiento.aplicadoValido("Número eliminado.");
    }

    /** Tells if the player solved the game: the board is full and has no repeated numbers.
     * @return true if the game is solved
     */

    public boolean estaResuelto() {
        return tablero.estaLleno() && ReglasSudoku.esTableroValido(tablero);
    }

    /** Returns all the cells that repeat a number in their row, column or block.
     * @return the cells in conflict (empty if none)
     */

    public Set<Posicion> getConflictos() {
        return ReglasSudoku.conflictos(tablero);
    }

    /**
     * Gives a hint: a number that can be placed in an empty cell without breaking
     * the rules. It does not place the number; the player decides. Each hint uses
     * one of the limited hints, so the board cannot be finished using only hints.
     *
     * @return the hint, or null if there are no hints left or no cell can receive a number
     */

    public Sugerencia pedirAyuda() {
        // No hints left
        if (ayudasRestantes <= 0) {
            // The rules class does the checking
            return null;
        }
        // Collect all the empty cells
        List<Posicion> vacias = new ArrayList<>();
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                if (tablero.estaVacia(fila, columna)) {
                    vacias.add(new Posicion(fila, columna));
                }
            }
        }
        // Mix them, so the hint is not always in the same place
        Collections.shuffle(vacias, random);
        // Look for the first empty cell that can receive a number
        for (Posicion posicion : vacias) {
            // Numbers that follow the rules in this cell
            List<Integer> candidatos = ReglasSudoku.candidatos(tablero,
                    posicion.getFila(), posicion.getColumna());
            // Found a cell with at least one possible number
            if (!candidatos.isEmpty()) {
                // Choose one of the possible numbers at random
                int valor = candidatos.get(random.nextInt(candidatos.size()));
                // Using a hint costs one
                ayudasRestantes--;
                return new Sugerencia(posicion, valor, ayudasRestantes);
            }
        }
        // No empty cell can receive a number
        return null;
    }

    /** Creates a new board with the generator and gives all the hints again. */
    private void iniciarPartida() {
        // Ask the generator for a new board
        this.tablero = generador.generar();
        // Reset the hints
        this.ayudasRestantes = AYUDAS_POR_PARTIDA;
    }
}
