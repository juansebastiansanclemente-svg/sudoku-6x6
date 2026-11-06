package sudoku.model;

final class TestBoards {

    static final int[][] SOLUTION = {
            {1, 2, 3, 4, 5, 6},
            {4, 5, 6, 1, 2, 3},
            {2, 3, 1, 5, 6, 4},
            {5, 6, 4, 2, 3, 1},
            {3, 1, 2, 6, 4, 5},
            {6, 4, 5, 3, 1, 2}
    };

    private TestBoards() {
    }

    static Board fullSolution() {
        Board board = new Board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.set(row, column, SOLUTION[row][column]);
            }
        }
        return board;
    }

    static Board fixedSolution() {
        Board board = new Board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.markFixed(row, column, SOLUTION[row][column]);
            }
        }
        return board;
    }

    static Board solutionWithEmptyCells(Position... emptyCells) {
        Board board = new Board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                if (!contains(emptyCells, row, column)) {
                    board.markFixed(row, column, SOLUTION[row][column]);
                }
            }
        }
        return board;
    }

    private static boolean contains(Position[] positions, int row, int column) {
        for (Position position : positions) {
            if (position.getRow() == row && position.getColumn() == column) {
                return true;
            }
        }
        return false;
    }
}
