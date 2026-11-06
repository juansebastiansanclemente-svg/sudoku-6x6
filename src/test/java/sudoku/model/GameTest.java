package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    @Test
    void ingresaUnNumeroCorrecto() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(new Position(0, 0)));

        MovementResult resultado = game.makeMove(0, 0, 1);

        assertTrue(resultado.isApplied());
        assertTrue(resultado.isValid());
        assertTrue(resultado.getConflicts().isEmpty());
        assertEquals(1, game.getBoard().get(0, 0));
    }

    @Test
    void ingresaUnNumeroIncorrectoYDevuelveConflictos() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(new Position(0, 0)));

        MovementResult resultado = game.makeMove(0, 0, 2);

        assertTrue(resultado.isApplied());
        assertFalse(resultado.isValid());
        assertTrue(resultado.getConflicts().contains(new Position(0, 0)));
        assertTrue(resultado.getConflicts().contains(new Position(0, 1)));
        assertEquals(2, game.getBoard().get(0, 0));
    }

    @Test
    void noPermiteModificarUnaPistaFija() {
        Game game = new Game(TablerosDePrueba.solucionFija());

        MovementResult resultado = game.makeMove(0, 0, 6);

        assertFalse(resultado.isApplied());
        assertFalse(resultado.isValid());
        assertEquals(1, game.getBoard().get(0, 0));
    }

    @Test
    void rechazaValoresYPosicionesInvalidas() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(new Position(0, 0)));

        assertFalse(game.makeMove(0, 0, 0).isApplied());
        assertFalse(game.makeMove(0, 0, 7).isApplied());
        assertFalse(game.makeMove(-1, 0, 1).isApplied());
        assertFalse(game.makeMove(0, 6, 1).isApplied());
        assertTrue(game.getBoard().isEmpty(0, 0));
    }

    @Test
    void borraUnNumeroIngresado() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(new Position(0, 0)));
        game.makeMove(0, 0, 1);

        MovementResult resultado = game.erase(0, 0);

        assertTrue(resultado.isApplied());
        assertTrue(resultado.isValid());
        assertTrue(game.getBoard().isEmpty(0, 0));
    }

    @Test
    void noBorraUnaPistaFijaNiUnaCeldaVacia() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(new Position(0, 0)));

        assertFalse(game.erase(0, 1).isApplied());
        assertFalse(game.erase(0, 0).isApplied());
        assertEquals(2, game.getBoard().get(0, 1));
    }

    @Test
    void detectaCuandoSeCompletaElTablero() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(new Position(5, 5)));

        MovementResult resultado = game.makeMove(5, 5, 2);

        assertTrue(resultado.isValid());
        assertTrue(game.isSolved());
        assertTrue(resultado.getMessage().contains("completado"));
    }

    @Test
    void unTableroLlenoConConflictosNoEstaResuelto() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(new Position(0, 0)));

        game.makeMove(0, 0, 2);

        assertTrue(game.getBoard().isFull());
        assertFalse(game.isSolved());
        assertFalse(game.getConflicts().isEmpty());
    }

    @Test
    void laAyudaTieneTresUsosYNoModificaElTablero() {
        Game game = new Game(TablerosDePrueba.solucionConVacios(
                new Position(0, 0), new Position(1, 1), new Position(2, 2),
                new Position(3, 3), new Position(4, 4), new Position(5, 5)));

        for (int uso = 1; uso <= Game.HINTS_PER_GAME; uso++) {
            Hint hint = game.getHint();

            assertNotNull(hint);
            assertTrue(game.getBoard().isEmpty(
                    hint.getPosition().getRow(), hint.getPosition().getColumn()));
            assertTrue(SudokuRules.isValidMove(game.getBoard(),
                    hint.getPosition().getRow(), hint.getPosition().getColumn(),
                    hint.getValue()));
            assertEquals(Game.HINTS_PER_GAME - uso, hint.remainingHint());
        }

        assertEquals(0, game.getRemainingHints());
        assertNull(game.getHint());
        assertEquals(6, game.getBoard().countEmptyCells());
    }

    @Test
    void laAyudaNoSeGastaSiNoHayCeldasVacias() {
        Game game = new Game(TablerosDePrueba.solucionFija());

        assertNull(game.getHint());
        assertEquals(Game.HINTS_PER_GAME, game.getRemainingHints());
    }

    @Test
    void unaNuevaPartidaReiniciaLasAyudas() {
        Game game = new Game(new SudokuGenerator(new Random(7)), new Random(8));

        game.getHint();
        game.getHint();
        assertEquals(1, game.getRemainingHints());

        Board nuevo = game.startNewGame();

        assertEquals(Game.HINTS_PER_GAME, game.getRemainingHints());
        assertEquals(12, nuevo.countFixedCells());
        assertFalse(game.isSolved());
    }

    @Test
    void exigeUnTableroInicial() {
        assertThrows(IllegalArgumentException.class, () -> new Game((Board) null));
        assertThrows(IllegalArgumentException.class, () -> new Game((BoardGenerator) null));
    }
}
