package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SudokuRulesTest {

    @Test
    void unaSolucionCompletaEsValida() {
        Board board = TablerosDePrueba.solucionFija();

        assertTrue(SudokuRules.isValidBoard(board));
        assertTrue(SudokuRules.conflicts(board).isEmpty());
    }

    @Test
    void unTableroVacioEsValido() {
        assertTrue(SudokuRules.isValidBoard(new Board()));
    }

    @Test
    void detectaConflictoEnUnaFila() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(0, 3, 1);

        Set<Position> conflictos = SudokuRules.conflicts(board);

        assertEquals(2, conflictos.size());
        assertTrue(conflictos.contains(new Position(0, 0)));
        assertTrue(conflictos.contains(new Position(0, 3)));
    }

    @Test
    void detectaConflictoEnUnaColumna() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(3, 0, 1);

        Set<Position> conflictos = SudokuRules.conflicts(board);

        assertEquals(2, conflictos.size());
        assertTrue(conflictos.contains(new Position(0, 0)));
        assertTrue(conflictos.contains(new Position(3, 0)));
    }

    @Test
    void detectaConflictoEnUnBloque() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(1, 2, 1);

        Set<Position> conflictos = SudokuRules.conflicts(board);

        assertEquals(2, conflictos.size());
        assertTrue(conflictos.contains(new Position(0, 0)));
        assertTrue(conflictos.contains(new Position(1, 2)));
    }

    @Test
    void noMarcaCeldasSinConflictos() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(0, 4, 2);

        assertTrue(SudokuRules.conflicts(board).isEmpty());
    }

    @Test
    void conflictosDeIncluyeLaCeldaYSusDuplicados() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(0, 3, 1);
        board.set(2, 0, 1);

        Set<Position> conflictos = SudokuRules.conflictsOf(board, 0, 0);

        assertEquals(3, conflictos.size());
        assertTrue(conflictos.contains(new Position(0, 0)));
        assertTrue(conflictos.contains(new Position(0, 3)));
        assertTrue(conflictos.contains(new Position(2, 0)));
    }

    @Test
    void conflictosDeDevuelveVacioSiNoHayDuplicados() {
        Board board = new Board();
        board.set(0, 0, 1);

        assertTrue(SudokuRules.conflictsOf(board, 0, 0).isEmpty());
        assertTrue(SudokuRules.conflictsOf(board, 1, 1).isEmpty());
    }

    @Test
    void validaMovimientosSegunLasReglas() {
        Board board = new Board();
        board.set(0, 0, 1);

        assertTrue(SudokuRules.isValidMove(board, 1, 3, 2));
        assertFalse(SudokuRules.isValidMove(board, 0, 3, 1));
        assertFalse(SudokuRules.isValidMove(board, 3, 0, 1));
        assertFalse(SudokuRules.isValidMove(board, 1, 2, 1));
        assertFalse(SudokuRules.isValidMove(board, 1, 1, 0));
        assertFalse(SudokuRules.isValidMove(board, 1, 1, 7));
        assertFalse(SudokuRules.isValidMove(board, 6, 1, 2));
    }

    @Test
    void calculaLosCandidatosDeUnaCelda() {
        Board board = new Board();

        assertEquals(List.of(1, 2, 3, 4, 5, 6), SudokuRules.candidates(board, 0, 0));

        board.set(0, 0, 1);
        board.set(1, 0, 2);
        board.set(0, 1, 3);

        assertEquals(List.of(4, 5, 6), SudokuRules.candidates(board, 1, 1));
        assertTrue(SudokuRules.candidates(board, -1, 0).isEmpty());
    }
}
