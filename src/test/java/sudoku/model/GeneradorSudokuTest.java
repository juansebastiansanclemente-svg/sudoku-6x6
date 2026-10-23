package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneradorSudokuTest {

    @Test
    void generaDocePistasDosPorBloque() {
        GeneradorTablero generador = new GeneradorSudoku(new Random(12345));

        for (int intento = 0; intento < 100; intento++) {
            Tablero tablero = generador.generar();

            assertEquals(12, tablero.contarFijas());
            assertEquals(24, tablero.contarVacias());
            assertTrue(ReglasSudoku.esTableroValido(tablero));

            int[] pistasPorBloque = new int[6];
            for (int fila = 0; fila < Tablero.TAMANO; fila++) {
                for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                    if (tablero.esFija(fila, columna)) {
                        pistasPorBloque[Tablero.numeroBloque(fila, columna)]++;
                        int valor = tablero.get(fila, columna);
                        assertTrue(valor >= Tablero.VALOR_MINIMO && valor <= Tablero.VALOR_MAXIMO);
                    }
                }
            }
            for (int pistas : pistasPorBloque) {
                assertEquals(2, pistas);
            }
        }
    }

    @Test
    void generaTablerosDiferentes() {
        GeneradorTablero generador = new GeneradorSudoku(new Random(99));
        Set<String> tableros = new HashSet<>();

        for (int intento = 0; intento < 20; intento++) {
            tableros.add(Arrays.deepToString(generador.generar().copiarCeldas()));
        }

        assertTrue(tableros.size() > 1);
    }

    @Test
    void exigeUnGeneradorAleatorio() {
        assertThrows(IllegalArgumentException.class, () -> new GeneradorSudoku(null));
    }

    @Test
    void cumpleLaInterfazGeneradorTablero() {
        GeneradorTablero generador = new GeneradorSudoku(new Random(1));

        assertNotNull(generador.generar());
    }
}
