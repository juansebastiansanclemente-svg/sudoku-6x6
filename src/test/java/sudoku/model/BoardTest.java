package sudoku.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void unTableroNuevoEstaVacio() {
        Board board = new Board();

        assertFalse(board.isFull());
        assertEquals(0, board.countFixedCells());
        assertEquals(36, board.countEmptyCells());
        assertTrue(board.isEmpty(0, 0));
        assertFalse(board.isFixed(0, 0));
    }

    @Test
    void permiteEscribirYLeerValores() {
        Board board = new Board();

        board.set(2, 3, 5);

        assertEquals(5, board.get(2, 3));
        assertFalse(board.isEmpty(2, 3));
    }

    @Test
    void unaCeldaFijaNoSePuedeModificar() {
        Board board = new Board();
        board.fix(0, 0, 1);

        assertTrue(board.isFixed(0, 0));
        assertThrows(IllegalStateException.class, () -> board.set(0, 0, 2));
        assertEquals(1, board.get(0, 0));
    }

    @Test
    void rechazaValoresFueraDeRango() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.set(0, 0, 7));
        assertThrows(IllegalArgumentException.class, () -> board.set(0, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> board.fix(0, 0, 0));
    }

    @Test
    void rechazaPosicionesFueraDelTablero() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.get(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.set(6, 0, 1));
        assertFalse(sudoku.model.Board.isOnBoard(0, 6));
    }

    @Test
    void detectaTableroLleno() {
        Board board = new Board();
        for (int fila = 0; fila < sudoku.model.Board.SIZE; fila++) {
            for (int columna = 0; columna < sudoku.model.Board.SIZE; columna++) {
                board.set(fila, columna, 1);
            }
        }

        assertTrue(board.isFull());
        assertEquals(0, board.countEmptyCells());
    }

    @Test
    void copiarNoComparteLaMatriz() {
        Board original = new Board();
        original.fix(0, 0, 3);

        Board copia = original.copy();
        copia.set(1, 1, 4);

        assertEquals(3, copia.get(0, 0));
        assertTrue(copia.isFixed(0, 0));
        assertTrue(original.isEmpty(1, 1));
    }

    @Test
    void limpiarJugadasConservaLasPistasFijas() {
        Board board = new Board();
        board.fix(0, 0, 1);
        board.set(1, 1, 2);

        board.clearPlayerMoves();

        assertEquals(1, board.get(0, 0));
        assertTrue(board.isEmpty(1, 1));
    }

    @Test
    void identificaCeldasDelMismoBloque() {
        assertTrue(sudoku.model.Board.isInSameBlock(0, 0, 1, 2));
        assertFalse(sudoku.model.Board.isInSameBlock(0, 0, 2, 0));
        assertFalse(sudoku.model.Board.isInSameBlock(0, 0, 0, 3));

        assertEquals(0, sudoku.model.Board.getBlockNumber(0, 0));
        assertEquals(1, sudoku.model.Board.getBlockNumber(0, 3));
        assertEquals(2, sudoku.model.Board.getBlockNumber(2, 0));
        assertEquals(5, sudoku.model.Board.getBlockNumber(4, 5));
    }
}
