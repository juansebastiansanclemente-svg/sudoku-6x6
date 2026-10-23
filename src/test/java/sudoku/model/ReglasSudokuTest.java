package sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglasSudokuTest {

    @Test
    void unaSolucionCompletaEsValida() {
        Tablero tablero = TablerosDePrueba.solucionFija();

        assertTrue(ReglasSudoku.esTableroValido(tablero));
        assertTrue(ReglasSudoku.conflictos(tablero).isEmpty());
    }

    @Test
    void unTableroVacioEsValido() {
        assertTrue(ReglasSudoku.esTableroValido(new Tablero()));
    }

    @Test
    void detectaConflictoEnUnaFila() {
        Tablero tablero = new Tablero();
        tablero.set(0, 0, 1);
        tablero.set(0, 3, 1);

        Set<Posicion> conflictos = ReglasSudoku.conflictos(tablero);

        assertEquals(2, conflictos.size());
        assertTrue(conflictos.contains(new Posicion(0, 0)));
        assertTrue(conflictos.contains(new Posicion(0, 3)));
    }

    @Test
    void detectaConflictoEnUnaColumna() {
        Tablero tablero = new Tablero();
        tablero.set(0, 0, 1);
        tablero.set(3, 0, 1);

        Set<Posicion> conflictos = ReglasSudoku.conflictos(tablero);

        assertEquals(2, conflictos.size());
        assertTrue(conflictos.contains(new Posicion(0, 0)));
        assertTrue(conflictos.contains(new Posicion(3, 0)));
    }

    @Test
    void detectaConflictoEnUnBloque() {
        Tablero tablero = new Tablero();
        tablero.set(0, 0, 1);
        tablero.set(1, 2, 1);

        Set<Posicion> conflictos = ReglasSudoku.conflictos(tablero);

        assertEquals(2, conflictos.size());
        assertTrue(conflictos.contains(new Posicion(0, 0)));
        assertTrue(conflictos.contains(new Posicion(1, 2)));
    }

    @Test
    void noMarcaCeldasSinConflictos() {
        Tablero tablero = new Tablero();
        tablero.set(0, 0, 1);
        tablero.set(0, 4, 2);

        assertTrue(ReglasSudoku.conflictos(tablero).isEmpty());
    }

    @Test
    void conflictosDeIncluyeLaCeldaYSusDuplicados() {
        Tablero tablero = new Tablero();
        tablero.set(0, 0, 1);
        tablero.set(0, 3, 1);
        tablero.set(2, 0, 1);

        Set<Posicion> conflictos = ReglasSudoku.conflictosDe(tablero, 0, 0);

        assertEquals(3, conflictos.size());
        assertTrue(conflictos.contains(new Posicion(0, 0)));
        assertTrue(conflictos.contains(new Posicion(0, 3)));
        assertTrue(conflictos.contains(new Posicion(2, 0)));
    }

    @Test
    void conflictosDeDevuelveVacioSiNoHayDuplicados() {
        Tablero tablero = new Tablero();
        tablero.set(0, 0, 1);

        assertTrue(ReglasSudoku.conflictosDe(tablero, 0, 0).isEmpty());
        assertTrue(ReglasSudoku.conflictosDe(tablero, 1, 1).isEmpty());
    }

    @Test
    void validaMovimientosSegunLasReglas() {
        Tablero tablero = new Tablero();
        tablero.set(0, 0, 1);

        assertTrue(ReglasSudoku.esMovimientoValido(tablero, 1, 3, 2));
        assertFalse(ReglasSudoku.esMovimientoValido(tablero, 0, 3, 1));
        assertFalse(ReglasSudoku.esMovimientoValido(tablero, 3, 0, 1));
        assertFalse(ReglasSudoku.esMovimientoValido(tablero, 1, 2, 1));
        assertFalse(ReglasSudoku.esMovimientoValido(tablero, 1, 1, 0));
        assertFalse(ReglasSudoku.esMovimientoValido(tablero, 1, 1, 7));
        assertFalse(ReglasSudoku.esMovimientoValido(tablero, 6, 1, 2));
    }

    @Test
    void calculaLosCandidatosDeUnaCelda() {
        Tablero tablero = new Tablero();

        assertEquals(List.of(1, 2, 3, 4, 5, 6), ReglasSudoku.candidatos(tablero, 0, 0));

        tablero.set(0, 0, 1);
        tablero.set(1, 0, 2);
        tablero.set(0, 1, 3);

        assertEquals(List.of(4, 5, 6), ReglasSudoku.candidatos(tablero, 1, 1));
        assertTrue(ReglasSudoku.candidatos(tablero, -1, 0).isEmpty());
    }
}
