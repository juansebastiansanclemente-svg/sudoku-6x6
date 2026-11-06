package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SudokuGeneratorTest {

    private static final int TABLEROS_VERIFICADOS = 2000;

    @Test
    void generaDocePistasDosPorBloque() {
        BoardGenerator generador = new SudokuGenerator(new Random(12345));

        for (int intento = 0; intento < 100; intento++) {
            Board board = generador.generate();

            assertEquals(12, board.countFixedCells());
            assertEquals(24, board.countEmptyCells());
            assertTrue(SudokuRules.isValidBoard(board));

            int[] pistasPorBloque = new int[6];
            for (int fila = 0; fila < sudoku.model.Board.SIZE; fila++) {
                for (int columna = 0; columna < sudoku.model.Board.SIZE; columna++) {
                    if (board.isFixed(fila, columna)) {
                        pistasPorBloque[sudoku.model.Board.getBlockNumber(fila, columna)]++;
                        int valor = board.get(fila, columna);
                        assertTrue(valor >= sudoku.model.Board.MIN_VALUE && valor <= sudoku.model.Board.MAX_VALUE);
                    }
                }
            }
            for (int pistas : pistasPorBloque) {
                assertEquals(2, pistas);
            }
        }
    }

    @Test
    void todosLosTablerosGeneradosSePuedenResolver() {
        BoardGenerator generador = new SudokuGenerator(new Random(2024));

        for (int intento = 0; intento < TABLEROS_VERIFICADOS; intento++) {
            Board board = generador.generate();
            Board solucion = board.copy();

            assertTrue(resolver(solucion, 0), "Tablero sin solución:\n" + board);
            assertTrue(solucion.isFull());
            assertTrue(SudokuRules.isValidBoard(solucion));

            for (int fila = 0; fila < sudoku.model.Board.SIZE; fila++) {
                for (int columna = 0; columna < sudoku.model.Board.SIZE; columna++) {
                    if (board.isFixed(fila, columna)) {
                        assertEquals(board.get(fila, columna), solucion.get(fila, columna));
                    }
                }
            }
        }
    }

    @Test
    void generaTablerosDiferentes() {
        BoardGenerator generador = new SudokuGenerator(new Random(99));
        Set<String> tableros = new HashSet<>();

        for (int intento = 0; intento < 20; intento++) {
            tableros.add(Arrays.deepToString(generador.generate().copyCells()));
        }

        assertTrue(tableros.size() > 1);
    }

    @Test
    void exigeUnGeneradorAleatorio() {
        assertThrows(IllegalArgumentException.class, () -> new SudokuGenerator(null));
    }

    @Test
    void cumpleLaInterfazGeneradorTablero() {
        BoardGenerator generador = new SudokuGenerator(new Random(1));

        assertNotNull(generador.generate());
    }

    private boolean resolver(Board board, int indice) {
        if (indice == sudoku.model.Board.SIZE * sudoku.model.Board.SIZE) {
            return true;
        }

        int fila = indice / sudoku.model.Board.SIZE;
        int columna = indice % sudoku.model.Board.SIZE;
        if (!board.isEmpty(fila, columna)) {
            return resolver(board, indice + 1);
        }

        for (int valor = sudoku.model.Board.MIN_VALUE; valor <= sudoku.model.Board.MAX_VALUE; valor++) {
            if (SudokuRules.isValidMove(board, fila, columna, valor)) {
                board.set(fila, columna, valor);
                if (resolver(board, indice + 1)) {
                    return true;
                }
                board.set(fila, columna, sudoku.model.Board.EMPTY);
            }
        }
        return false;
    }
}
