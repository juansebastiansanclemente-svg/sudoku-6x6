package sudoku.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ResultadoMovimiento {

    private final boolean aplicado;
    private final boolean valido;
    private final List<Posicion> conflictos;
    private final String mensaje;

    private ResultadoMovimiento(boolean aplicado, boolean valido, Collection<Posicion> conflictos, String mensaje) {
        this.aplicado = aplicado;
        this.valido = valido;
        this.conflictos = Collections.unmodifiableList(new ArrayList<>(conflictos));
        this.mensaje = mensaje;
    }

    public static ResultadoMovimiento aplicadoValido(String mensaje) {
        return new ResultadoMovimiento(true, true, List.of(), mensaje);
    }

    public static ResultadoMovimiento aplicadoConConflictos(Collection<Posicion> conflictos, String mensaje) {
        return new ResultadoMovimiento(true, false, conflictos, mensaje);
    }

    public static ResultadoMovimiento rechazado(String mensaje) {
        return new ResultadoMovimiento(false, false, List.of(), mensaje);
    }

    public boolean isAplicado() {
        return aplicado;
    }

    public boolean isValido() {
        return valido;
    }

    public List<Posicion> getConflictos() {
        return conflictos;
    }

    public String getMensaje() {
        return mensaje;
    }
}
