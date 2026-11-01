package sudoku.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Result of a move made by the player. It tells whether the move was applied,
 * whether it follows the Sudoku rules, which cells are in conflict, and a
 * message to show. It cannot be changed after it is created.
 */

public final class ResultadoMovimiento {
    /** True if the move changed the board. */
    private final boolean aplicado;
    /** True if the move does not break any Sudoku rule. */
    private final boolean valido;
    /** Cells that break the rules because of this move (empty if none). */
    private final List<Posicion> conflictos;
    /** Message that explains the result to the player. */
    private final String mensaje;

    /**
     * Creates a result. It is private: use the static methods below instead.
     *
     * @param aplicado   true if the move changed the board
     * @param valido     true if the move follows the Sudoku rules
     * @param conflictos cells in conflict
     * @param mensaje    message for the player
     */

    private ResultadoMovimiento(boolean aplicado, boolean valido, Collection<Posicion> conflictos, String mensaje) {
        // Save the simple values
        this.aplicado = aplicado;
        this.valido = valido;
        // Copy the list and make it read-only, so nobody can change it from outside
        this.conflictos = Collections.unmodifiableList(new ArrayList<>(conflictos));
        this.mensaje = mensaje;
    }

    /** Creates a result for a move that was applied and follows the rules.
     * @param mensaje message for the player @return the result
     */

    public static ResultadoMovimiento aplicadoValido(String mensaje) {
        // Applied and valid: no conflicts
        return new ResultadoMovimiento(true, true, List.of(), mensaje);
    }

    /** Creates a result for a move that was applied but breaks the rules.
     * @param conflictos cells in conflict
     * @param mensaje message for the player
     * @return the result
     */

    public static ResultadoMovimiento aplicadoConConflictos(Collection<Posicion> conflictos, String mensaje) {
        // Applied but not valid: send the conflicting cells
        return new ResultadoMovimiento(true, false, conflictos, mensaje);
    }

    /** Creates a result for a move that was not applied (for example, a fixed cell).
     * @param mensaje message that explains why it was rejected
     * @return the result
     */

    public static ResultadoMovimiento rechazado(String mensaje) {
        // Not applied and not valid: the board stays the same
        return new ResultadoMovimiento(false, false, List.of(), mensaje);
    }

    /** Tells if the move changed the board.
     * @return true if it was applied
     */

    public boolean isAplicado() {
        return aplicado;
    }

    /** Tells if the move follows the Sudoku rules.
     * @return true if it is valid
     */

    public boolean isValido() {
        return valido;
    }

    /** Returns the cells in conflict. The list cannot be modified.
     * @return the cells in conflict (empty if none)
     */

    public List<Posicion> getConflictos() {
        return conflictos;
    }

    /** Returns the message for the player.
     * @return the message
     */

    public String getMensaje() {
        return mensaje;
    }
}
