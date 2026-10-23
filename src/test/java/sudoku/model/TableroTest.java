package sudoku.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableroTest {

    @Test
    void unTableroNuevoEstaVacio() {
        Tablero tablero = new Tablero();

        assertFalse(tablero.estaLleno());
        assertEquals(0, tablero.contarFijas());
        assertEquals(36, tablero.contarVacias());
        assertTrue(tablero.estaVacia(0, 0));
        assertFalse(tablero.esFija(0, 0));
    }

    @Test
    void permiteEscribirYLeerValores() {
        Tablero tablero = new Tablero();

        tablero.set(2, 3, 5);

        assertEquals(5, tablero.get(2, 3));
        assertFalse(tablero.estaVacia(2, 3));
    }

    @Test
    void unaCeldaFijaNoSePuedeModificar() {
        Tablero tablero = new Tablero();
        tablero.fijar(0, 0, 1);

        assertTrue(tablero.esFija(0, 0));
        assertThrows(IllegalStateException.class, () -> tablero.set(0, 0, 2));
        assertEquals(1, tablero.get(0, 0));
    }

    @Test
    void rechazaValoresFueraDeRango() {
        Tablero tablero = new Tablero();

        assertThrows(IllegalArgumentException.class, () -> tablero.set(0, 0, 7));
        assertThrows(IllegalArgumentException.class, () -> tablero.set(0, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> tablero.fijar(0, 0, 0));
    }

    @Test
    void rechazaPosicionesFueraDelTablero() {
        Tablero tablero = new Tablero();

        assertThrows(IllegalArgumentException.class, () -> tablero.get(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> tablero.set(6, 0, 1));
        assertFalse(Tablero.dentroDelTablero(0, 6));
    }

    @Test
    void detectaTableroLleno() {
        Tablero tablero = new Tablero();
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                tablero.set(fila, columna, 1);
            }
        }

        assertTrue(tablero.estaLleno());
        assertEquals(0, tablero.contarVacias());
    }

    @Test
    void copiarNoComparteLaMatriz() {
        Tablero original = new Tablero();
        original.fijar(0, 0, 3);

        Tablero copia = original.copiar();
        copia.set(1, 1, 4);

        assertEquals(3, copia.get(0, 0));
        assertTrue(copia.esFija(0, 0));
        assertTrue(original.estaVacia(1, 1));
    }

    @Test
    void limpiarJugadasConservaLasPistasFijas() {
        Tablero tablero = new Tablero();
        tablero.fijar(0, 0, 1);
        tablero.set(1, 1, 2);

        tablero.limpiarJugadas();

        assertEquals(1, tablero.get(0, 0));
        assertTrue(tablero.estaVacia(1, 1));
    }

    @Test
    void identificaCeldasDelMismoBloque() {
        assertTrue(Tablero.mismoBloque(0, 0, 1, 2));
        assertFalse(Tablero.mismoBloque(0, 0, 2, 0));
        assertFalse(Tablero.mismoBloque(0, 0, 0, 3));

        assertEquals(0, Tablero.numeroBloque(0, 0));
        assertEquals(1, Tablero.numeroBloque(0, 3));
        assertEquals(2, Tablero.numeroBloque(2, 0));
        assertEquals(5, Tablero.numeroBloque(4, 5));
    }
}
