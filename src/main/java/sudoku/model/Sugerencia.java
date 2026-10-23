package sudoku.model;

public final class Sugerencia {

    private final Posicion posicion;
    private final int valor;
    private final int ayudasRestantes;

    public Sugerencia(Posicion posicion, int valor, int ayudasRestantes) {
        this.posicion = posicion;
        this.valor = valor;
        this.ayudasRestantes = ayudasRestantes;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public int getValor() {
        return valor;
    }

    public int getAyudasRestantes() {
        return ayudasRestantes;
    }

    @Override
    public String toString() {
        return "Sugerencia: coloca " + valor + " en " + posicion;
    }
}
