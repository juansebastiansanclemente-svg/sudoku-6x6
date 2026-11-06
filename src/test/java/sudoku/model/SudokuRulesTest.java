package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SudokuRulesTest {

    @Test
    void completeSolutionIsValid() {
        Board board = TestBoards.fixedSolution();

        assertTrue(SudokuRules.isValidBoard(board));
        assertTrue(SudokuRules.findConflicts(board).isEmpty());
    }

    @Test
    void emptyBoardIsValid() {
        assertTrue(SudokuRules.isValidBoard(new Board()));
    }

    @Test
    void detectsConflictInRow() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(0, 3, 1);

        Set<Position> conflicts = SudokuRules.findConflicts(board);

        assertEquals(2, conflicts.size());
        assertTrue(conflicts.contains(new Position(0, 0)));
        assertTrue(conflicts.contains(new Position(0, 3)));
    }

    @Test
    void detectsConflictInColumn() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(3, 0, 1);

        Set<Position> conflicts = SudokuRules.findConflicts(board);

        assertEquals(2, conflicts.size());
        assertTrue(conflicts.contains(new Position(0, 0)));
        assertTrue(conflicts.contains(new Position(3, 0)));
    }

    @Test
    void detectsConflictInBlock() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(1, 2, 1);

        Set<Position> conflicts = SudokuRules.findConflicts(board);

        assertEquals(2, conflicts.size());
        assertTrue(conflicts.contains(new Position(0, 0)));
        assertTrue(conflicts.contains(new Position(1, 2)));
    }

    @Test
    void doesNotMarkCellsWithoutConflicts() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(0, 4, 2);

        assertTrue(SudokuRules.findConflicts(board).isEmpty());
    }

    @Test
    void findConflictsOfIncludesCellAndDuplicates() {
        Board board = new Board();
        board.set(0, 0, 1);
        board.set(0, 3, 1);
        board.set(2, 0, 1);

        Set<Position> conflicts = SudokuRules.findConflictsOf(board, 0, 0);

        assertEquals(3, conflicts.size());
        assertTrue(conflicts.contains(new Position(0, 0)));
        assertTrue(conflicts.contains(new Position(0, 3)));
        assertTrue(conflicts.contains(new Position(2, 0)));
    }

    @Test
    void findConflictsOfReturnsEmptyWhenNoDuplicates() {
        Board board = new Board();
        board.set(0, 0, 1);

        assertTrue(SudokuRules.findConflictsOf(board, 0, 0).isEmpty());
        assertTrue(SudokuRules.findConflictsOf(board, 1, 1).isEmpty());
    }

    @Test
    void validatesMovesAccordingToRules() {
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
    void calculatesCandidatesOfCell() {
        Board board = new Board();

        assertEquals(List.of(1, 2, 3, 4, 5, 6), SudokuRules.findCandidates(board, 0, 0));

        board.set(0, 0, 1);
        board.set(1, 0, 2);
        board.set(0, 1, 3);

        assertEquals(List.of(4, 5, 6), SudokuRules.findCandidates(board, 1, 1));
        assertTrue(SudokuRules.findCandidates(board, -1, 0).isEmpty());
    }
}
