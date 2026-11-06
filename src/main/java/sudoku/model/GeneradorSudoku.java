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
public class GeneradorSudoku implements GeneradorTablero {

    /** Number of visible clues left in each 2x3 block. */
    public static final int PISTAS_POR_BLOQUE = 2;

    /** Source of randomness used for the solution and the clues. */
    private final Random random;

    /** Creates a generator with a new random source. */
    public GeneradorSudoku() {
        this(new Random());
    }

    /** Creates a generator with the given random source.
     * @param random source of randomness
     * @throws IllegalArgumentException if the random source is null
     */
    public GeneradorSudoku(Random random) {
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
    public Tablero generar() {
        Tablero solucion = generarSolucion();
        Tablero tablero = new Tablero();

        for (Posicion posicion : elegirPosiciones()) {
            tablero.fijar(posicion.getFila(), posicion.getColumna(),
                    solucion.get(posicion.getFila(), posicion.getColumna()));
        }
        return tablero;
    }

    /** Fills the whole board with a valid solution using backtracking. */
    private Tablero generarSolucion() {
        Tablero solucion = new Tablero();
        if (!resolver(solucion, 0)) {
            throw new IllegalStateException("No fue posible generar una solución completa.");
        }
        return solucion;
    }

    /** Tries random numbers in the next cell and backtracks when a number fails. */
    private boolean resolver(Tablero tablero, int indice) {
        if (indice == Tablero.TAMANO * Tablero.TAMANO) {
            return true;
        }

        int fila = indice / Tablero.TAMANO;
        int columna = indice % Tablero.TAMANO;
        List<Integer> valores = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        Collections.shuffle(valores, random);

        for (int valor : valores) {
            if (ReglasSudoku.esMovimientoValido(tablero, fila, columna, valor)) {
                tablero.set(fila, columna, valor);
                if (resolver(tablero, indice + 1)) {
                    return true;
                }
                tablero.set(fila, columna, Tablero.VACIA);
            }
        }
        return false;
    }

    /** Chooses two random cells from each 2x3 block to show as clues. */
    private List<Posicion> elegirPosiciones() {
        List<Posicion> posiciones = new ArrayList<>();
        for (int filaBloque = 0; filaBloque < Tablero.TAMANO; filaBloque += Tablero.FILAS_POR_BLOQUE) {
            for (int columnaBloque = 0; columnaBloque < Tablero.TAMANO; columnaBloque += Tablero.COLUMNAS_POR_BLOQUE) {
                List<Posicion> celdasBloque = new ArrayList<>();
                for (int fila = filaBloque; fila < filaBloque + Tablero.FILAS_POR_BLOQUE; fila++) {
                    for (int columna = columnaBloque; columna < columnaBloque + Tablero.COLUMNAS_POR_BLOQUE; columna++) {
                        celdasBloque.add(new Posicion(fila, columna));
                    }
                }
                Collections.shuffle(celdasBloque, random);
                posiciones.addAll(celdasBloque.subList(0, PISTAS_POR_BLOQUE));
            }
        }
        return posiciones;
    }
}
