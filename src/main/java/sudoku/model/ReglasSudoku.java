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

public final class ReglasSudoku {

    /** Private on purpose: this class only offers static methods, so no object is needed. */

    private ReglasSudoku() {
    }

    /** Tells if a number can be placed in a cell without repeating it in its row, column or block.
     * @param tablero the board to check
     * @param fila row of the cell
     * @param columna column of the cell
     * @param valor number to place (1 to 6)
     * @return true if the move follows the rules
     */

    public static boolean esMovimientoValido(Tablero tablero, int fila, int columna, int valor) {
        // A position outside the board is never valid
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return false;
        }
        // Only numbers from 1 to 6 are allowed
        if (valor < Tablero.VALOR_MINIMO || valor > Tablero.VALOR_MAXIMO) {
            return false;
        }
        // Valid only if the number is not in the row, the column or the block
        return !existeEnFila(tablero, fila, columna, valor)
                && !existeEnColumna(tablero, columna, fila, valor)
                && !existeEnBloque(tablero, fila, columna, valor);
    }

    /** Tells if a number is already in a row.
     * @param tablero the board to check
     * @param fila row to look in
     * @param valor number to look for
     * @return true if the number is in the row
     */

    public static boolean existeEnFila(Tablero tablero, int fila, int valor) {
        // -1 means no column is skipped: look in the whole row
        return existeEnFila(tablero, fila, -1, valor);
    }

    /** Tells if a number is already in a column.
     * @param tablero the board to check
     * @param columna column to look in
     * @param valor number to look for
     * @return true if the number is in the column
     */

    public static boolean existeEnColumna(Tablero tablero, int columna, int valor) {
        // -1 means no row is skipped: look in the whole column
        return existeEnColumna(tablero, columna, -1, valor);
    }

    /** Tells if a number is already in the block of a cell, ignoring the cell itself.
     * @param tablero the board to check
     * @param fila row of the cell
     * @param columna column of the cell
     * @param valor number to look for
     * @return true if another cell of the block has the number
     */

    public static boolean existeEnBloque(Tablero tablero, int fila, int columna, int valor) {
        // First row and first column of the block where the cell is
        int filaInicial = (fila / Tablero.FILAS_POR_BLOQUE) * Tablero.FILAS_POR_BLOQUE;
        int columnaInicial = (columna / Tablero.COLUMNAS_POR_BLOQUE) * Tablero.COLUMNAS_POR_BLOQUE;
        // Go through every cell of the block
        for (int f = filaInicial; f < filaInicial + Tablero.FILAS_POR_BLOQUE; f++) {
            for (int c = columnaInicial; c < columnaInicial + Tablero.COLUMNAS_POR_BLOQUE; c++) {
                // Skip the cell being checked, so it does not conflict with itself
                if ((f != fila || c != columna) && tablero.get(f, c) == valor) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds all the cells that repeat a number in their row, column or block.
     *
     * @param tablero the board to check
     * @return the cells in conflict (empty if the board has no repeated numbers)
     */

    public static Set<Posicion> conflictos(Tablero tablero) {
        // A set does not keep repeated cells, so each conflict appears once
        Set<Posicion> conflictos = new LinkedHashSet<>();
        // Rows: compare each cell with the ones to its right
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                int valor = tablero.get(fila, columna);
                // Empty cells cannot be in conflict
                if (valor == Tablero.VACIA) {
                    continue;
                }
                for (int otraColumna = columna + 1; otraColumna < Tablero.TAMANO; otraColumna++) {
                    // Same number twice in the row: both cells are in conflict
                    if (tablero.get(fila, otraColumna) == valor) {
                        conflictos.add(new Posicion(fila, columna));
                        conflictos.add(new Posicion(fila, otraColumna));
                    }
                }
            }
        }

        for (int columna = 0; columna < Tablero.TAMANO; columna++) {
            for (int fila = 0; fila < Tablero.TAMANO; fila++) {
                int valor = tablero.get(fila, columna);
                if (valor == Tablero.VACIA) {
                    continue;
                }
                // Columns: compare each cell with the ones below it
                for (int otraFila = fila + 1; otraFila < Tablero.TAMANO; otraFila++) {
                    if (tablero.get(otraFila, columna) == valor) {
                        conflictos.add(new Posicion(fila, columna));
                        conflictos.add(new Posicion(otraFila, columna));
                    }
                }
            }
        }
        // Blocks: go through each block of 2x3
        for (int filaBloque = 0; filaBloque < Tablero.TAMANO; filaBloque += Tablero.FILAS_POR_BLOQUE) {
            for (int columnaBloque = 0; columnaBloque < Tablero.TAMANO; columnaBloque += Tablero.COLUMNAS_POR_BLOQUE) {
                // List the 6 cells of this block
                List<Posicion> celdas = new ArrayList<>();
                for (int fila = filaBloque; fila < filaBloque + Tablero.FILAS_POR_BLOQUE; fila++) {
                    for (int columna = columnaBloque; columna < columnaBloque + Tablero.COLUMNAS_POR_BLOQUE; columna++) {
                        celdas.add(new Posicion(fila, columna));
                    }
                }
                // Compare each cell of the block with the ones after it
                for (int i = 0; i < celdas.size(); i++) {
                    Posicion actual = celdas.get(i);
                    int valor = tablero.get(actual.getFila(), actual.getColumna());
                    if (valor == Tablero.VACIA) {
                        continue;
                    }
                    for (int j = i + 1; j < celdas.size(); j++) {
                        Posicion otra = celdas.get(j);
                        if (tablero.get(otra.getFila(), otra.getColumna()) == valor) {
                            conflictos.add(actual);
                            conflictos.add(otra);
                        }
                    }
                }
            }
        }

        return conflictos;
    }

    /**
     * Finds the cells that repeat the number of one specific cell, in its row,
     * column or block. The cell itself is included when there is a conflict.
     *
     * @param tablero the board to check
     * @param fila    row of the cell
     * @param columna column of the cell
     * @return the cells in conflict with that cell (empty if there is none)
     */

    public static Set<Posicion> conflictosDe(Tablero tablero, int fila, int columna) {
        int valor = tablero.get(fila, columna);
        // An empty cell has no conflicts
        if (valor == Tablero.VACIA) {
            return Collections.emptySet();
        }

        Set<Posicion> conflictos = new LinkedHashSet<>();
        // Same row: other cells with the same number
        for (int c = 0; c < Tablero.TAMANO; c++) {
            if (c != columna && tablero.get(fila, c) == valor) {
                conflictos.add(new Posicion(fila, c));
            }
        }
        // Same column: other cells with the same number
        for (int f = 0; f < Tablero.TAMANO; f++) {
            if (f != fila && tablero.get(f, columna) == valor) {
                conflictos.add(new Posicion(f, columna));
            }
        }
        // Same block: other cells with the same number
        for (int f = 0; f < Tablero.TAMANO; f++) {
            for (int c = 0; c < Tablero.TAMANO; c++) {
                if ((f != fila || c != columna)
                        && Tablero.mismoBloque(fila, columna, f, c)
                        && tablero.get(f, c) == valor) {
                    conflictos.add(new Posicion(f, c));
                }
            }
        }
        // No repeated number: nothing to mark
        if (conflictos.isEmpty()) {
            return conflictos;
        }
        // Add the cell itself too, so it can also be highlighted
        conflictos.add(new Posicion(fila, columna));
        return conflictos;
    }

    /**
     * Lists the numbers that can be placed in a cell following the rules.
     *
     * @param tablero the board to check
     * @param fila    row of the cell
     * @param columna column of the cell
     * @return the possible numbers (empty if there is none or the position is outside the board)
     */

    public static List<Integer> candidatos(Tablero tablero, int fila, int columna) {
        List<Integer> candidatos = new ArrayList<>();
        // Outside the board: no numbers can be placed
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return candidatos;
        }
        // Try each number from 1 to 6
        for (int valor = Tablero.VALOR_MINIMO; valor <= Tablero.VALOR_MAXIMO; valor++) {
            // Keep the numbers that do not repeat in row, column or block
            if (esMovimientoValido(tablero, fila, columna, valor)) {
                candidatos.add(valor);
            }
        }
        return candidatos;
    }

    /**
     * Tells if the board has no repeated numbers in any row, column or block.
     *
     * @param tablero the board to check
     * @return true if there are no conflicts
     */

    public static boolean esTableroValido(Tablero tablero) {
        // The board is valid when there are no cells in conflict
        return conflictos(tablero).isEmpty();
    }

    /** Looks for a number in a row, skipping one column.
     * @param tablero the board to check
     * @param fila row to look in
     * @param columnaExcluida column to skip (-1 to skip none)
     * @param valor number to look for
     * @return true if the number is found
     */

    private static boolean existeEnFila(Tablero tablero, int fila, int columnaExcluida, int valor) {
        for (int columna = 0; columna < Tablero.TAMANO; columna++) {
            // Skip the excluded cell and compare the others
            if (columna != columnaExcluida && tablero.get(fila, columna) == valor) {

                return true;
            }
        }
        return false;
    }

    /** Looks for a number in a column, skipping one row.
     * @param tablero the board to check
     * @param columna column to look in
     * @param filaExcluida row to skip (-1 to skip none)
     * @param valor number to look for
     * @return true if the number is found
     */

    private static boolean existeEnColumna(Tablero tablero, int columna, int filaExcluida, int valor) {
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            // Skip the excluded cell and compare the others
            if (fila != filaExcluida && tablero.get(fila, columna) == valor) {
                return true;
            }
        }
        return false;
    }
}
