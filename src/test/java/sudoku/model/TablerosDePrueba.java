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

    static Tablero solucionSinFijar() {
        Tablero tablero = new Tablero();
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                tablero.set(fila, columna, SOLUCION[fila][columna]);
            }
        }
        return tablero;
    }

    static Tablero solucionFija() {
        Tablero tablero = new Tablero();
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                tablero.fijar(fila, columna, SOLUCION[fila][columna]);
            }
        }
        return tablero;
    }

    static Tablero solucionConVacios(Posicion... vacias) {
        Tablero tablero = new Tablero();
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                if (!contiene(vacias, fila, columna)) {
                    tablero.fijar(fila, columna, SOLUCION[fila][columna]);
                }
            }
        }
        return tablero;
    }

    private static boolean contiene(Posicion[] posiciones, int fila, int columna) {
        for (Posicion posicion : posiciones) {
            if (posicion.getFila() == fila && posicion.getColumna() == columna) {
                return true;
            }
        }
        return false;
    }
}
