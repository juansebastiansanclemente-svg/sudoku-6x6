package sudoku.model;

final class TablerosDePrueba {

    static final int[][] SOLUCION = {
            {1, 2, 3, 4, 5, 6},
            {4, 5, 6, 1, 2, 3},
            {2, 3, 1, 5, 6, 4},
            {5, 6, 4, 2, 3, 1},
            {3, 1, 2, 6, 4, 5},
            {6, 4, 5, 3, 1, 2}
    };

    private TablerosDePrueba() {
    }

    static Board solucionSinFijar() {
        Board board = new Board();
        for (int fila = 0; fila < sudoku.model.Board.SIZE; fila++) {
            for (int columna = 0; columna < sudoku.model.Board.SIZE; columna++) {
                board.set(fila, columna, SOLUCION[fila][columna]);
            }
        }
        return board;
    }

    static Board solucionFija() {
        Board board = new Board();
        for (int fila = 0; fila < sudoku.model.Board.SIZE; fila++) {
            for (int columna = 0; columna < sudoku.model.Board.SIZE; columna++) {
                board.fix(fila, columna, SOLUCION[fila][columna]);
            }
        }
        return board;
    }

    static Board solucionConVacios(Position... vacias) {
        Board board = new Board();
        for (int fila = 0; fila < sudoku.model.Board.SIZE; fila++) {
            for (int columna = 0; columna < sudoku.model.Board.SIZE; columna++) {
                if (!contiene(vacias, fila, columna)) {
                    board.fix(fila, columna, SOLUCION[fila][columna]);
                }
            }
        }
        return board;
    }

    private static boolean contiene(Position[] posiciones, int fila, int columna) {
        for (Position position : posiciones) {
            if (position.getRow() == fila && position.getColumn() == columna) {
                return true;
            }
        }
        return false;
    }
}
