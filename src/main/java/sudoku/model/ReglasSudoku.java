package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ReglasSudoku {

    private ReglasSudoku() {
    }

    public static boolean esMovimientoValido(Tablero tablero, int fila, int columna, int valor) {
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return false;
        }
        if (valor < Tablero.VALOR_MINIMO || valor > Tablero.VALOR_MAXIMO) {
            return false;
        }
        return !existeEnFila(tablero, fila, columna, valor)
                && !existeEnColumna(tablero, columna, fila, valor)
                && !existeEnBloque(tablero, fila, columna, valor);
    }

    public static boolean existeEnFila(Tablero tablero, int fila, int valor) {
        return existeEnFila(tablero, fila, -1, valor);
    }

    public static boolean existeEnColumna(Tablero tablero, int columna, int valor) {
        return existeEnColumna(tablero, columna, -1, valor);
    }

    public static boolean existeEnBloque(Tablero tablero, int fila, int columna, int valor) {
        int filaInicial = (fila / Tablero.FILAS_POR_BLOQUE) * Tablero.FILAS_POR_BLOQUE;
        int columnaInicial = (columna / Tablero.COLUMNAS_POR_BLOQUE) * Tablero.COLUMNAS_POR_BLOQUE;
        for (int f = filaInicial; f < filaInicial + Tablero.FILAS_POR_BLOQUE; f++) {
            for (int c = columnaInicial; c < columnaInicial + Tablero.COLUMNAS_POR_BLOQUE; c++) {
                if ((f != fila || c != columna) && tablero.get(f, c) == valor) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Set<Posicion> conflictos(Tablero tablero) {
        Set<Posicion> conflictos = new LinkedHashSet<>();

        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                int valor = tablero.get(fila, columna);
                if (valor == Tablero.VACIA) {
                    continue;
                }
                for (int otraColumna = columna + 1; otraColumna < Tablero.TAMANO; otraColumna++) {
                    if (tablero.get(fila, otraColumna) == valor) {
                        conflictos.add(new Posicion(fila, columna));
                        conflictos.add(new Posicion(fila, otraColumna));
                    }
                }
            }
        }

        for (int columna = 0; columna < Tablero.TAMANO; columna++) {
            for (int fila = 0; fila < Tablero.TAMANO; fila++) {
                int valor = tablero.get(fila, columna);
                if (valor == Tablero.VACIA) {
                    continue;
                }
                for (int otraFila = fila + 1; otraFila < Tablero.TAMANO; otraFila++) {
                    if (tablero.get(otraFila, columna) == valor) {
                        conflictos.add(new Posicion(fila, columna));
                        conflictos.add(new Posicion(otraFila, columna));
                    }
                }
            }
        }

        for (int filaBloque = 0; filaBloque < Tablero.TAMANO; filaBloque += Tablero.FILAS_POR_BLOQUE) {
            for (int columnaBloque = 0; columnaBloque < Tablero.TAMANO; columnaBloque += Tablero.COLUMNAS_POR_BLOQUE) {
                List<Posicion> celdas = new ArrayList<>();
                for (int fila = filaBloque; fila < filaBloque + Tablero.FILAS_POR_BLOQUE; fila++) {
                    for (int columna = columnaBloque; columna < columnaBloque + Tablero.COLUMNAS_POR_BLOQUE; columna++) {
                        celdas.add(new Posicion(fila, columna));
                    }
                }
                for (int i = 0; i < celdas.size(); i++) {
                    Posicion actual = celdas.get(i);
                    int valor = tablero.get(actual.getFila(), actual.getColumna());
                    if (valor == Tablero.VACIA) {
                        continue;
                    }
                    for (int j = i + 1; j < celdas.size(); j++) {
                        Posicion otra = celdas.get(j);
                        if (tablero.get(otra.getFila(), otra.getColumna()) == valor) {
                            conflictos.add(actual);
                            conflictos.add(otra);
                        }
                    }
                }
            }
        }

        return conflictos;
    }

    public static Set<Posicion> conflictosDe(Tablero tablero, int fila, int columna) {
        int valor = tablero.get(fila, columna);
        if (valor == Tablero.VACIA) {
            return Collections.emptySet();
        }

        Set<Posicion> conflictos = new LinkedHashSet<>();
        for (int c = 0; c < Tablero.TAMANO; c++) {
            if (c != columna && tablero.get(fila, c) == valor) {
                conflictos.add(new Posicion(fila, c));
            }
        }
        for (int f = 0; f < Tablero.TAMANO; f++) {
            if (f != fila && tablero.get(f, columna) == valor) {
                conflictos.add(new Posicion(f, columna));
            }
        }
        for (int f = 0; f < Tablero.TAMANO; f++) {
            for (int c = 0; c < Tablero.TAMANO; c++) {
                if ((f != fila || c != columna)
                        && Tablero.mismoBloque(fila, columna, f, c)
                        && tablero.get(f, c) == valor) {
                    conflictos.add(new Posicion(f, c));
                }
            }
        }

        if (conflictos.isEmpty()) {
            return conflictos;
        }
        conflictos.add(new Posicion(fila, columna));
        return conflictos;
    }

    public static List<Integer> candidatos(Tablero tablero, int fila, int columna) {
        List<Integer> candidatos = new ArrayList<>();
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return candidatos;
        }
        for (int valor = Tablero.VALOR_MINIMO; valor <= Tablero.VALOR_MAXIMO; valor++) {
            if (esMovimientoValido(tablero, fila, columna, valor)) {
                candidatos.add(valor);
            }
        }
        return candidatos;
    }

    public static boolean esTableroValido(Tablero tablero) {
        return conflictos(tablero).isEmpty();
    }

    private static boolean existeEnFila(Tablero tablero, int fila, int columnaExcluida, int valor) {
        for (int columna = 0; columna < Tablero.TAMANO; columna++) {
            if (columna != columnaExcluida && tablero.get(fila, columna) == valor) {
                return true;
            }
        }
        return false;
    }

    private static boolean existeEnColumna(Tablero tablero, int columna, int filaExcluida, int valor) {
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            if (fila != filaExcluida && tablero.get(fila, columna) == valor) {
                return true;
            }
        }
        return false;
    }
}
