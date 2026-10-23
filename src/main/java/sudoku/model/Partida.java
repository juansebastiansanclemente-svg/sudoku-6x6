package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Partida {

    public static final int AYUDAS_POR_PARTIDA = 3;

    private final GeneradorTablero generador;
    private final Random random;
    private Tablero tablero;
    private int ayudasRestantes;

    public Partida() {
        this(new GeneradorSudoku());
    }

    public Partida(GeneradorTablero generador) {
        this(generador, new Random());
    }

    public Partida(GeneradorTablero generador, Random random) {
        if (generador == null || random == null) {
            throw new IllegalArgumentException("El generador y el aleatorio no pueden ser nulos.");
        }
        this.generador = generador;
        this.random = random;
        iniciarPartida();
    }

    public Partida(Tablero tableroInicial) {
        if (tableroInicial == null) {
            throw new IllegalArgumentException("El tablero inicial no puede ser nulo.");
        }
        this.generador = new GeneradorSudoku();
        this.random = new Random();
        this.tablero = tableroInicial;
        this.ayudasRestantes = AYUDAS_POR_PARTIDA;
    }

    public Tablero nuevaPartida() {
        iniciarPartida();
        return tablero;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public int getAyudasRestantes() {
        return ayudasRestantes;
    }

    public ResultadoMovimiento ingresar(int fila, int columna, int valor) {
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return ResultadoMovimiento.rechazado("La posición está fuera del tablero.");
        }
        if (valor < Tablero.VALOR_MINIMO || valor > Tablero.VALOR_MAXIMO) {
            return ResultadoMovimiento.rechazado("Solo se permiten números del 1 al 6.");
        }
        if (tablero.esFija(fila, columna)) {
            return ResultadoMovimiento.rechazado("Esa celda es una pista fija y no se puede modificar.");
        }

        tablero.set(fila, columna, valor);
        Set<Posicion> conflictos = ReglasSudoku.conflictosDe(tablero, fila, columna);
        if (conflictos.isEmpty()) {
            if (estaResuelto()) {
                return ResultadoMovimiento.aplicadoValido("¡Tablero completado correctamente!");
            }
            return ResultadoMovimiento.aplicadoValido("Número colocado.");
        }
        return ResultadoMovimiento.aplicadoConConflictos(conflictos,
                "El número " + valor + " incumple las reglas del Sudoku.");
    }

    public ResultadoMovimiento borrar(int fila, int columna) {
        if (!Tablero.dentroDelTablero(fila, columna)) {
            return ResultadoMovimiento.rechazado("La posición está fuera del tablero.");
        }
        if (tablero.esFija(fila, columna)) {
            return ResultadoMovimiento.rechazado("Esa celda es una pista fija y no se puede modificar.");
        }
        if (tablero.estaVacia(fila, columna)) {
            return ResultadoMovimiento.rechazado("La celda ya está vacía.");
        }

        tablero.set(fila, columna, Tablero.VACIA);
        return ResultadoMovimiento.aplicadoValido("Número eliminado.");
    }

    public boolean estaResuelto() {
        return tablero.estaLleno() && ReglasSudoku.esTableroValido(tablero);
    }

    public Set<Posicion> getConflictos() {
        return ReglasSudoku.conflictos(tablero);
    }

    public Sugerencia pedirAyuda() {
        if (ayudasRestantes <= 0) {
            return null;
        }

        List<Posicion> vacias = new ArrayList<>();
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                if (tablero.estaVacia(fila, columna)) {
                    vacias.add(new Posicion(fila, columna));
                }
            }
        }
        Collections.shuffle(vacias, random);

        for (Posicion posicion : vacias) {
            List<Integer> candidatos = ReglasSudoku.candidatos(tablero,
                    posicion.getFila(), posicion.getColumna());
            if (!candidatos.isEmpty()) {
                int valor = candidatos.get(random.nextInt(candidatos.size()));
                ayudasRestantes--;
                return new Sugerencia(posicion, valor, ayudasRestantes);
            }
        }
        return null;
    }

    private void iniciarPartida() {
        this.tablero = generador.generar();
        this.ayudasRestantes = AYUDAS_POR_PARTIDA;
    }
}
