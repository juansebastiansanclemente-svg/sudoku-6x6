package sudoku.model;

import java.util.Arrays;

public class Tablero {

    public static final int TAMANO = 6;
    public static final int FILAS_POR_BLOQUE = 2;
    public static final int COLUMNAS_POR_BLOQUE = 3;
    public static final int VALOR_MINIMO = 1;
    public static final int VALOR_MAXIMO = 6;
    public static final int VACIA = 0;

    private final int[][] celdas;
    private final boolean[][] fijas;

    public Tablero() {
        celdas = new int[TAMANO][TAMANO];
        fijas = new boolean[TAMANO][TAMANO];
    }

    public int get(int fila, int columna) {
        validarPosicion(fila, columna);
        return celdas[fila][columna];
    }

    public void set(int fila, int columna, int valor) {
        validarPosicion(fila, columna);
        validarValor(valor);
        if (fijas[fila][columna] && celdas[fila][columna] != valor) {
            throw new IllegalStateException("No se puede modificar una celda fija.");
        }
        celdas[fila][columna] = valor;
    }

    public void fijar(int fila, int columna, int valor) {
        validarPosicion(fila, columna);
        if (valor < VALOR_MINIMO || valor > VALOR_MAXIMO) {
            throw new IllegalArgumentException("El valor debe estar entre 1 y 6.");
        }
        celdas[fila][columna] = valor;
        fijas[fila][columna] = true;
    }

    public boolean esFija(int fila, int columna) {
        validarPosicion(fila, columna);
        return fijas[fila][columna];
    }

    public boolean estaVacia(int fila, int columna) {
        validarPosicion(fila, columna);
        return celdas[fila][columna] == VACIA;
    }

    public boolean estaLleno() {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (celdas[fila][columna] == VACIA) {
                    return false;
                }
            }
        }
        return true;
    }

    public int contarFijas() {
        int total = 0;
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (fijas[fila][columna]) {
                    total++;
                }
            }
        }
        return total;
    }

    public int contarVacias() {
        int total = 0;
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (celdas[fila][columna] == VACIA) {
                    total++;
                }
            }
        }
        return total;
    }

    public void limpiar() {
        for (int fila = 0; fila < TAMANO; fila++) {
            Arrays.fill(celdas[fila], VACIA);
            Arrays.fill(fijas[fila], false);
        }
    }

    public void limpiarJugadas() {
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int columna = 0; columna < TAMANO; columna++) {
                if (!fijas[fila][columna]) {
                    celdas[fila][columna] = VACIA;
                }
            }
        }
    }

    public Tablero copiar() {
        Tablero copia = new Tablero();
        for (int fila = 0; fila < TAMANO; fila++) {
            System.arraycopy(celdas[fila], 0, copia.celdas[fila], 0, TAMANO);
            System.arraycopy(fijas[fila], 0, copia.fijas[fila], 0, TAMANO);
        }
        return copia;
    }

    public int[][] copiarCeldas() {
        int[][] copia = new int[TAMANO][TAMANO];
        for (int fila = 0; fila < TAMANO; fila++) {
            System.arraycopy(celdas[fila], 0, copia[fila], 0, TAMANO);
        }
        return copia;
    }

    public static boolean dentroDelTablero(int fila, int columna) {
        return fila >= 0 && fila < TAMANO && columna >= 0 && columna < TAMANO;
    }

    public static boolean mismoBloque(int fila1, int columna1, int fila2, int columna2) {
        return fila1 / FILAS_POR_BLOQUE == fila2 / FILAS_POR_BLOQUE
                && columna1 / COLUMNAS_POR_BLOQUE == columna2 / COLUMNAS_POR_BLOQUE;
    }

    public static int numeroBloque(int fila, int columna) {
        int bloquesPorFila = TAMANO / COLUMNAS_POR_BLOQUE;
        return (fila / FILAS_POR_BLOQUE) * bloquesPorFila + (columna / COLUMNAS_POR_BLOQUE);
    }

    private void validarPosicion(int fila, int columna) {
        if (!dentroDelTablero(fila, columna)) {
            throw new IllegalArgumentException("Posición fuera del tablero: (" + fila + ", " + columna + ")");
        }
    }

    private void validarValor(int valor) {
        if (valor < VACIA || valor > VALOR_MAXIMO) {
            throw new IllegalArgumentException("El valor debe estar entre 0 y 6.");
        }
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int fila = 0; fila < TAMANO; fila++) {
            if (fila > 0 && fila % FILAS_POR_BLOQUE == 0) {
                texto.append("------+------\n");
            }
            for (int columna = 0; columna < TAMANO; columna++) {
                if (columna > 0 && columna % COLUMNAS_POR_BLOQUE == 0) {
                    texto.append("| ");
                }
                texto.append(celdas[fila][columna] == VACIA ? "." : celdas[fila][columna]).append(' ');
            }
            texto.append('\n');
        }
        return texto.toString();
    }
}
