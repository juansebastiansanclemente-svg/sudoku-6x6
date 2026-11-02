package sudoku.model;

import java.util.Arrays;

/**
 * The 6x6 Sudoku board. It stores the number of each cell and which cells are
 * fixed (the initial clues that the player cannot change). The board is split
 * into 6 blocks of 2 rows by 3 columns. Rows and columns start at 0.
 */

public class Tablero {

    /** Number of rows and columns of the board. */
    public static final int TAMANO = 6;
    /** Rows in each block. */
    public static final int FILAS_POR_BLOQUE = 2;
    /** Columns in each block. */
    public static final int COLUMNAS_POR_BLOQUE = 3;
    /** Smallest number a cell can hold. */
    public static final int VALOR_MINIMO = 1;
    /** Biggest number a cell can hold. */
    public static final int VALOR_MAXIMO = 6;
    /** Value that means the cell is empty. */
    public static final int VACIA = 0;

    /** Number in each cell (0 if empty). */
    private final int[][] celdas;
    /** True in the cells that are initial clues and cannot be changed. */
    private final boolean[][] fijas;

    /** Creates an empty board with no fixed cells. */
    public Tablero() {
        // All numbers start at 0 (empty)
        celdas = new int[TAMANO][TAMANO];
        // All cells start as not fixed
        fijas = new boolean[TAMANO][TAMANO];
    }

    /** Returns the number in a cell.
     * @param fila row of the cell
     * @param columna column of the cell
     * @return the number (0 if empty)
     * @throws IllegalArgumentException if the position is outside the board
     */

    public int get(int fila, int columna) {
        // Stop here if the position is outside the board
        validarPosicion(fila, columna);
        return celdas[fila][columna];
    }

    /** Puts a number in a cell. Use 0 to empty it.
     * @param fila row of the cell
     * @param columna column of the cell @param valor number from 1 to 6, or 0 to empty the cell
     * @throws IllegalArgumentException if the position or the number is not valid
     * @throws IllegalStateException if the cell is fixed and the number is different
     */

    public void set(int fila, int columna, int valor) {
        // Stop here if the position is outside the board
        validarPosicion(fila, columna);
        // Stop here if the number is not between 0 and 6
        validarValor(valor);
        // A fixed cell can only be "set" to its own number
        if (fijas[fila][columna] && celdas[fila][columna] != valor) {
            throw new IllegalStateException("No se puede modificar una celda fija.");
        }
        celdas[fila][columna] = valor;
    }

    /** Puts a number in a cell and marks it as fixed (an initial clue).
     * @param fila row of the cell
     * @param columna column of the cell
     * @param valor number from 1 to 6
     * @throws IllegalArgumentException if the position or the number is not valid
     */

    public void fijar(int fila, int columna, int valor) {
        // Stop here if the position is outside the board
        validarPosicion(fila, columna);
        // Fixed clues must be a real number (1 to 6), never empty
        if (valor < VALOR_MINIMO || valor > VALOR_MAXIMO) {
            throw new IllegalArgumentException("El valor debe estar entre 1 y 6.");
        }
        celdas[fila][columna] = valor;
        // Mark it as a clue the player cannot change
        fijas[fila][columna] = true;
    }

    /** Tells if a cell is a fixed clue.
     * @param fila row of the cell
     * @param columna column of the cell
     * @return true if the cell is fixed
     */

    public boolean esFija(int fila, int columna) {
        // Stop here if the position is outside the board
        validarPosicion(fila, columna);
        return fijas[fila][columna];
    }

    /** Tells if a cell is empty.
     * @param fila row of the cell
     * @param columna column of the cell
     * @return true if the cell has no number
     */

    public boolean estaVacia(int fila, int columna) {
        // Stop here if the position is outside the board
        validarPosicion(fila, columna);
        return celdas[fila][columna] == VACIA;
    }

    /** Tells if every cell has a number.
     * @return true if there are no empty cells
     */

    public boolean estaLleno() {
        // Go through every cell
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (celdas[fila][columna] == VACIA) {
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

    public int contarFijas() {
        // Counter that grows each time a matching cell is found
        int total = 0;
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (fijas[fila][columna]) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Counts the empty cells.
     * @return number of empty cells
     */

    public int contarVacias() {
        // Counter that grows each time a matching cell is found
        int total = 0;
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (celdas[fila][columna] == VACIA) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Empties the whole board and removes all fixed clues.
     */

    public void limpiar() {
        for (int fila = 0; fila < TAMANO; fila++) {
            // Fill the row with 0 (empty) and not fixed
            Arrays.fill(celdas[fila], VACIA);
            Arrays.fill(fijas[fila], false);
        }
    }

    /** Empties only the cells filled by the player; the fixed clues stay.
     */

    public void limpiarJugadas() {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                // Only empty the cells that are not fixed clues
                if (!fijas[fila][columna]) {
                    celdas[fila][columna] = VACIA;
                }
            }
        }
    }

    /** Creates an independent copy of the board, including the fixed cells.
     * @return a new board with the same content
     */

    public Tablero copiar() {
        Tablero copia = new Tablero();
        for (int fila = 0; fila < TAMANO; fila++) {
            // Copy the row of numbers and the row of fixed marks
            System.arraycopy(celdas[fila], 0, copia.celdas[fila], 0, TAMANO);
            System.arraycopy(fijas[fila], 0, copia.fijas[fila], 0, TAMANO);
        }
        // The copy does not share data with the original, so changing one does not change the other
        return copia;
    }

    /** Copies only the numbers of the cells.
     * @return a new 6x6 array with the numbers
     */

    public int[][] copiarCeldas() {
        int[][] copia = new int[TAMANO][TAMANO];
        for (int fila = 0; fila < TAMANO; fila++) {
            // Copy row by row, so the new array is independent
            System.arraycopy(celdas[fila], 0, copia[fila], 0, TAMANO);
        }
        return copia;
    }

    /** Tells if a position is inside the board.
     * @param fila row to check
     * @param columna column to check
     * @return true if both are between 0 and 5
     */

    public static boolean dentroDelTablero(int fila, int columna) {
        return fila >= 0 && fila < TAMANO && columna >= 0 && columna < TAMANO;
    }

    /** Tells if two cells are in the same block of 2x3.
     * @param fila1 row of the first cell
     * @param columna1 column of the first cell
     * @param fila2 row of the second cell
     * @param columna2 column of the second cell
     * @return true if both cells are in the same block
     */

    public static boolean mismoBloque(int fila1, int columna1, int fila2, int columna2) {
        // Dividing by the block size gives the block row and block column; both must match
        return fila1 / FILAS_POR_BLOQUE == fila2 / FILAS_POR_BLOQUE
                && columna1 / COLUMNAS_POR_BLOQUE == columna2 / COLUMNAS_POR_BLOQUE;
    }

    /** Gives the number of the block of a cell. Blocks go from 0 to 5, left to right and top to bottom.
     * @param fila row of the cell
     * @param columna column of the cell
     * @return the block number
     */

    public static int numeroBloque(int fila, int columna) {
        // How many blocks fit side by side in one row of the board (6 / 3 = 2 (6 blocks, 3 columns, 2 rows))
        int bloquesPorFila = TAMANO / COLUMNAS_POR_BLOQUE;
        // Skip the full block rows above, then add the block column
        return (fila / FILAS_POR_BLOQUE) * bloquesPorFila + (columna / COLUMNAS_POR_BLOQUE);
    }

    /** Throws an error if the position is outside the board.
     * @param fila row to check
     * @param columna column to check
     * @throws IllegalArgumentException if the position is outside the board
     */

    private void validarPosicion(int fila, int columna) {
        if (!dentroDelTablero(fila, columna)) {
            throw new IllegalArgumentException("Posición fuera del tablero: (" + fila + ", " + columna + ")");
        }
    }

    /** Throws an error if the number is not between 0 and 6.
     * @param valor number to check
     * @throws IllegalArgumentException if the number is out of range
     */

    private void validarValor(int valor) {
        if (valor < VACIA || valor > VALOR_MAXIMO) {
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
        for (int fila = 0; fila < TAMANO; fila++) {
            // Draw a line between blocks of rows
            if (fila > 0 && fila % FILAS_POR_BLOQUE == 0) {
                texto.append("------+------\n");
            }
            for (int columna = 0; columna < TAMANO; columna++) {
                // Draw a bar between blocks of columns
                if (columna > 0 && columna % COLUMNAS_POR_BLOQUE == 0) {
                    texto.append("| ");
                }
                // Show "." for empty cells and the number for the others
                texto.append(celdas[fila][columna] == VACIA ? "." : celdas[fila][columna]).append(' ');
            }
            texto.append('\n');
        }
        return texto.toString();
    }
}
