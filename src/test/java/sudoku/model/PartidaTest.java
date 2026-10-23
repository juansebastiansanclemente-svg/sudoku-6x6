package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartidaTest {

    @Test
    void ingresaUnNumeroCorrecto() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(new Posicion(0, 0)));

        ResultadoMovimiento resultado = partida.ingresar(0, 0, 1);

        assertTrue(resultado.isAplicado());
        assertTrue(resultado.isValido());
        assertTrue(resultado.getConflictos().isEmpty());
        assertEquals(1, partida.getTablero().get(0, 0));
    }

    @Test
    void ingresaUnNumeroIncorrectoYDevuelveConflictos() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(new Posicion(0, 0)));

        ResultadoMovimiento resultado = partida.ingresar(0, 0, 2);

        assertTrue(resultado.isAplicado());
        assertFalse(resultado.isValido());
        assertTrue(resultado.getConflictos().contains(new Posicion(0, 0)));
        assertTrue(resultado.getConflictos().contains(new Posicion(0, 1)));
        assertEquals(2, partida.getTablero().get(0, 0));
    }

    @Test
    void noPermiteModificarUnaPistaFija() {
        Partida partida = new Partida(TablerosDePrueba.solucionFija());

        ResultadoMovimiento resultado = partida.ingresar(0, 0, 6);

        assertFalse(resultado.isAplicado());
        assertFalse(resultado.isValido());
        assertEquals(1, partida.getTablero().get(0, 0));
    }

    @Test
    void rechazaValoresYPosicionesInvalidas() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(new Posicion(0, 0)));

        assertFalse(partida.ingresar(0, 0, 0).isAplicado());
        assertFalse(partida.ingresar(0, 0, 7).isAplicado());
        assertFalse(partida.ingresar(-1, 0, 1).isAplicado());
        assertFalse(partida.ingresar(0, 6, 1).isAplicado());
        assertTrue(partida.getTablero().estaVacia(0, 0));
    }

    @Test
    void borraUnNumeroIngresado() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(new Posicion(0, 0)));
        partida.ingresar(0, 0, 1);

        ResultadoMovimiento resultado = partida.borrar(0, 0);

        assertTrue(resultado.isAplicado());
        assertTrue(resultado.isValido());
        assertTrue(partida.getTablero().estaVacia(0, 0));
    }

    @Test
    void noBorraUnaPistaFijaNiUnaCeldaVacia() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(new Posicion(0, 0)));

        assertFalse(partida.borrar(0, 1).isAplicado());
        assertFalse(partida.borrar(0, 0).isAplicado());
        assertEquals(2, partida.getTablero().get(0, 1));
    }

    @Test
    void detectaCuandoSeCompletaElTablero() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(new Posicion(5, 5)));

        ResultadoMovimiento resultado = partida.ingresar(5, 5, 2);

        assertTrue(resultado.isValido());
        assertTrue(partida.estaResuelto());
        assertTrue(resultado.getMensaje().contains("completado"));
    }

    @Test
    void unTableroLlenoConConflictosNoEstaResuelto() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(new Posicion(0, 0)));

        partida.ingresar(0, 0, 2);

        assertTrue(partida.getTablero().estaLleno());
        assertFalse(partida.estaResuelto());
        assertFalse(partida.getConflictos().isEmpty());
    }

    @Test
    void laAyudaTieneTresUsosYNoModificaElTablero() {
        Partida partida = new Partida(TablerosDePrueba.solucionConVacios(
                new Posicion(0, 0), new Posicion(1, 1), new Posicion(2, 2),
                new Posicion(3, 3), new Posicion(4, 4), new Posicion(5, 5)));

        for (int uso = 1; uso <= Partida.AYUDAS_POR_PARTIDA; uso++) {
            Sugerencia sugerencia = partida.pedirAyuda();

            assertNotNull(sugerencia);
            assertTrue(partida.getTablero().estaVacia(
                    sugerencia.getPosicion().getFila(), sugerencia.getPosicion().getColumna()));
            assertTrue(ReglasSudoku.esMovimientoValido(partida.getTablero(),
                    sugerencia.getPosicion().getFila(), sugerencia.getPosicion().getColumna(),
                    sugerencia.getValor()));
            assertEquals(Partida.AYUDAS_POR_PARTIDA - uso, sugerencia.getAyudasRestantes());
        }

        assertEquals(0, partida.getAyudasRestantes());
        assertNull(partida.pedirAyuda());
        assertEquals(6, partida.getTablero().contarVacias());
    }

    @Test
    void laAyudaNoSeGastaSiNoHayCeldasVacias() {
        Partida partida = new Partida(TablerosDePrueba.solucionFija());

        assertNull(partida.pedirAyuda());
        assertEquals(Partida.AYUDAS_POR_PARTIDA, partida.getAyudasRestantes());
    }

    @Test
    void unaNuevaPartidaReiniciaLasAyudas() {
        Partida partida = new Partida(new GeneradorSudoku(new Random(7)), new Random(8));

        partida.pedirAyuda();
        partida.pedirAyuda();
        assertEquals(1, partida.getAyudasRestantes());

        Tablero nuevo = partida.nuevaPartida();

        assertEquals(Partida.AYUDAS_POR_PARTIDA, partida.getAyudasRestantes());
        assertEquals(12, nuevo.contarFijas());
        assertFalse(partida.estaResuelto());
    }

    @Test
    void exigeUnTableroInicial() {
        assertThrows(IllegalArgumentException.class, () -> new Partida((Tablero) null));
        assertThrows(IllegalArgumentException.class, () -> new Partida((GeneradorTablero) null));
    }
}
