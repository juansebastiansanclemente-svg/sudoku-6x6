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
    void placesCorrectNumber() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(new Position(0, 0)));

        MoveResult result = game.placeNumber(0, 0, 1);

        assertTrue(result.isApplied());
        assertTrue(result.isValid());
        assertTrue(result.getConflicts().isEmpty());
        assertEquals(1, game.getBoard().get(0, 0));
    }

    @Test
    void placesWrongNumberAndReturnsConflicts() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(new Position(0, 0)));

        MoveResult result = game.placeNumber(0, 0, 2);

        assertTrue(result.isApplied());
        assertFalse(result.isValid());
        assertTrue(result.getConflicts().contains(new Position(0, 0)));
        assertTrue(result.getConflicts().contains(new Position(0, 1)));
        assertEquals(2, game.getBoard().get(0, 0));
    }

    @Test
    void doesNotAllowChangingFixedClue() {
        Game game = new Game(TestBoards.fixedSolution());

        MoveResult result = game.placeNumber(0, 0, 6);

        assertFalse(result.isApplied());
        assertFalse(result.isValid());
        assertEquals(1, game.getBoard().get(0, 0));
    }

    @Test
    void rejectsInvalidValuesAndPositions() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(new Position(0, 0)));

        assertFalse(game.placeNumber(0, 0, 0).isApplied());
        assertFalse(game.placeNumber(0, 0, 7).isApplied());
        assertFalse(game.placeNumber(-1, 0, 1).isApplied());
        assertFalse(game.placeNumber(0, 6, 1).isApplied());
        assertTrue(game.getBoard().isEmpty(0, 0));
    }

    @Test
    void clearsPlacedNumber() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(new Position(0, 0)));
        game.placeNumber(0, 0, 1);

        MoveResult result = game.clearCell(0, 0);

        assertTrue(result.isApplied());
        assertTrue(result.isValid());
        assertTrue(game.getBoard().isEmpty(0, 0));
    }

    @Test
    void doesNotClearFixedClueOrEmptyCell() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(new Position(0, 0)));

        assertFalse(game.clearCell(0, 1).isApplied());
        assertFalse(game.clearCell(0, 0).isApplied());
        assertEquals(2, game.getBoard().get(0, 1));
    }

    @Test
    void detectsWhenBoardIsCompleted() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(new Position(5, 5)));

        MoveResult result = game.placeNumber(5, 5, 2);

        assertTrue(result.isValid());
        assertTrue(game.isSolved());
        assertTrue(result.getMessage().contains("completado"));
    }

    @Test
    void fullBoardWithConflictsIsNotSolved() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(new Position(0, 0)));

        game.placeNumber(0, 0, 2);

        assertTrue(game.getBoard().isFull());
        assertFalse(game.isSolved());
        assertFalse(game.getConflicts().isEmpty());
    }

    @Test
    void hintHasThreeUsesAndDoesNotModifyBoard() {
        Game game = new Game(TestBoards.solutionWithEmptyCells(
                new Position(0, 0), new Position(1, 1), new Position(2, 2),
                new Position(3, 3), new Position(4, 4), new Position(5, 5)));

        for (int use = 1; use <= Game.HINTS_PER_GAME; use++) {
            Hint hint = game.requestHint();

            assertNotNull(hint);
            assertTrue(game.getBoard().isEmpty(
                    hint.getPosition().getRow(), hint.getPosition().getColumn()));
            assertTrue(SudokuRules.isValidMove(game.getBoard(),
                    hint.getPosition().getRow(), hint.getPosition().getColumn(),
                    hint.getValue()));
            assertEquals(Game.HINTS_PER_GAME - use, hint.getRemainingHints());
        }

        assertEquals(0, game.getRemainingHints());
        assertNull(game.requestHint());
        assertEquals(6, game.getBoard().countEmpty());
    }

    @Test
    void hintIsNotSpentWhenNoEmptyCells() {
        Game game = new Game(TestBoards.fixedSolution());

        assertNull(game.requestHint());
        assertEquals(Game.HINTS_PER_GAME, game.getRemainingHints());
    }

    @Test
    void newGameResetsHints() {
        Game game = new Game(new SudokuGenerator(new Random(7)), new Random(8));

        game.requestHint();
        game.requestHint();
        assertEquals(1, game.getRemainingHints());

        Board newBoard = game.newGame();

        assertEquals(Game.HINTS_PER_GAME, game.getRemainingHints());
        assertEquals(12, newBoard.countFixed());
        assertFalse(game.isSolved());
    }

    @Test
    void requiresInitialBoard() {
        assertThrows(IllegalArgumentException.class, () -> new Game((Board) null));
        assertThrows(IllegalArgumentException.class, () -> new Game((BoardGenerator) null));
    }
}
