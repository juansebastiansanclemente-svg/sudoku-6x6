package sudoku.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void newBoardIsEmpty() {
        Board board = new Board();

        assertFalse(board.isFull());
        assertEquals(0, board.countFixed());
        assertEquals(36, board.countEmpty());
        assertTrue(board.isEmpty(0, 0));
        assertFalse(board.isFixed(0, 0));
    }

    @Test
    void allowsWritingAndReadingValues() {
        Board board = new Board();

        board.set(2, 3, 5);

        assertEquals(5, board.get(2, 3));
        assertFalse(board.isEmpty(2, 3));
    }

    @Test
    void fixedCellCannotBeChanged() {
        Board board = new Board();
        board.markFixed(0, 0, 1);

        assertTrue(board.isFixed(0, 0));
        assertThrows(IllegalStateException.class, () -> board.set(0, 0, 2));
        assertEquals(1, board.get(0, 0));
    }

    @Test
    void rejectsValuesOutOfRange() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.set(0, 0, 7));
        assertThrows(IllegalArgumentException.class, () -> board.set(0, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> board.markFixed(0, 0, 0));
    }

    @Test
    void rejectsPositionsOutsideBoard() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.get(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.set(6, 0, 1));
        assertFalse(Board.isInsideBoard(0, 6));
    }

    @Test
    void detectsFullBoard() {
        Board board = new Board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.set(row, column, 1);
            }
        }

        assertTrue(board.isFull());
        assertEquals(0, board.countEmpty());
    }

    @Test
    void copyDoesNotShareArray() {
        Board original = new Board();
        original.markFixed(0, 0, 3);

        Board copy = original.copy();
        copy.set(1, 1, 4);

        assertEquals(3, copy.get(0, 0));
        assertTrue(copy.isFixed(0, 0));
        assertTrue(original.isEmpty(1, 1));
    }

    @Test
    void clearMovesKeepsFixedClues() {
        Board board = new Board();
        board.markFixed(0, 0, 1);
        board.set(1, 1, 2);

        board.clearMoves();

        assertEquals(1, board.get(0, 0));
        assertTrue(board.isEmpty(1, 1));
    }

    @Test
    void identifiesCellsInSameBlock() {
        assertTrue(Board.isSameBlock(0, 0, 1, 2));
        assertFalse(Board.isSameBlock(0, 0, 2, 0));
        assertFalse(Board.isSameBlock(0, 0, 0, 3));

        assertEquals(0, Board.getBlockNumber(0, 0));
        assertEquals(1, Board.getBlockNumber(0, 3));
        assertEquals(2, Board.getBlockNumber(2, 0));
        assertEquals(5, Board.getBlockNumber(4, 5));
    }
}
