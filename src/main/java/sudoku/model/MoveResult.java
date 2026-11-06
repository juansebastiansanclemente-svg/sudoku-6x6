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

public final class MoveResult {
    /** True if the move changed the board. */
    private final boolean applied;
    /** True if the move does not break any Sudoku rule. */
    private final boolean valid;
    /** Cells that break the rules because of this move (empty if none). */
    private final List<Position> conflicts;
    /** Message that explains the result to the player. */
    private final String message;

    /**
     * Creates a result. It is private: use the static methods below instead.
     *
     * @param applied   true if the move changed the board
     * @param valid     true if the move follows the Sudoku rules
     * @param conflicts cells in conflict
     * @param message   message for the player
     */

    private MoveResult(boolean applied, boolean valid, Collection<Position> conflicts, String message) {
        // Save the simple values
        this.applied = applied;
        this.valid = valid;
        // Copy the list and make it read-only, so nobody can change it from outside
        this.conflicts = Collections.unmodifiableList(new ArrayList<>(conflicts));
        this.message = message;
    }

    /** Creates a result for a move that was applied and follows the rules.
     * @param message message for the player
     * @return the result
     */

    public static MoveResult appliedValid(String message) {
        // Applied and valid: no conflicts
        return new MoveResult(true, true, List.of(), message);
    }

    /** Creates a result for a move that was applied but breaks the rules.
     * @param conflicts cells in conflict
     * @param message message for the player
     * @return the result
     */

    public static MoveResult appliedWithConflicts(Collection<Position> conflicts, String message) {
        // Applied but not valid: send the conflicting cells
        return new MoveResult(true, false, conflicts, message);
    }

    /** Creates a result for a move that was not applied (for example, a fixed cell).
     * @param message message that explains why it was rejected
     * @return the result
     */

    public static MoveResult rejected(String message) {
        // Not applied and not valid: the board stays the same
        return new MoveResult(false, false, List.of(), message);
    }

    /** Tells if the move changed the board.
     * @return true if it was applied
     */

    public boolean isApplied() {
        return applied;
    }

    /** Tells if the move follows the Sudoku rules.
     * @return true if it is valid
     */

    public boolean isValid() {
        return valid;
    }

    /** Returns the cells in conflict. The list cannot be modified.
     * @return the cells in conflict (empty if none)
     */

    public List<Position> getConflicts() {
        return conflicts;
    }

    /** Returns the message for the player.
     * @return the message
     */

    public String getMessage() {
        return message;
    }
}
