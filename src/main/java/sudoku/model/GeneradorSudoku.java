package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneradorSudoku implements GeneradorTablero {

    public static final int PISTAS_POR_BLOQUE = 2;
    private static final int INTENTOS_MAXIMOS = 200;

    private final Random random;

    public GeneradorSudoku() {
        this(new Random());
    }

    public GeneradorSudoku(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("El generador aleatorio no puede ser nulo.");
        }
        this.random = random;
    }

    @Override
    public Tablero generar() {
        for (int intento = 0; intento < INTENTOS_MAXIMOS; intento++) {
            Tablero tablero = new Tablero();
            List<Posicion> posiciones = elegirPosiciones();
            if (colocarPistas(tablero, posiciones, 0)) {
                for (Posicion posicion : posiciones) {
                    tablero.fijar(posicion.getFila(), posicion.getColumna(),
                            tablero.get(posicion.getFila(), posicion.getColumna()));
                }
                return tablero;
            }
        }
        throw new IllegalStateException("No fue posible generar un tablero válido.");
    }

    private List<Posicion> elegirPosiciones() {
        List<Posicion> posiciones = new ArrayList<>();
        for (int filaBloque = 0; filaBloque < Tablero.TAMANO; filaBloque += Tablero.FILAS_POR_BLOQUE) {
            for (int columnaBloque = 0; columnaBloque < Tablero.TAMANO; columnaBloque += Tablero.COLUMNAS_POR_BLOQUE) {
                List<Posicion> celdasBloque = new ArrayList<>();
                for (int fila = filaBloque; fila < filaBloque + Tablero.FILAS_POR_BLOQUE; fila++) {
                    for (int columna = columnaBloque; columna < columnaBloque + Tablero.COLUMNAS_POR_BLOQUE; columna++) {
                        celdasBloque.add(new Posicion(fila, columna));
                    }
                }
                Collections.shuffle(celdasBloque, random);
                posiciones.addAll(celdasBloque.subList(0, PISTAS_POR_BLOQUE));
            }
        }
        return posiciones;
    }

    private boolean colocarPistas(Tablero tablero, List<Posicion> posiciones, int indice) {
        if (indice == posiciones.size()) {
            return true;
        }

        Posicion posicion = posiciones.get(indice);
        List<Integer> valores = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        Collections.shuffle(valores, random);

        for (int valor : valores) {
            if (ReglasSudoku.esMovimientoValido(tablero, posicion.getFila(), posicion.getColumna(), valor)) {
                tablero.set(posicion.getFila(), posicion.getColumna(), valor);
                if (colocarPistas(tablero, posiciones, indice + 1)) {
                    return true;
                }
                tablero.set(posicion.getFila(), posicion.getColumna(), Tablero.VACIA);
            }
        }
        return false;
    }
}
