package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A Sudoku game. It keeps the board and the hints left, and it is the class the
 * controller talks to: it receives the moves of the player, checks them with the
 * Sudoku rules, and gives hints. The first board comes from a generator.
 */

public class Game {

    /** Number of hints the player can use in each game. */
    public static final int HINTS_PER_GAME = 3;

    /** Creates the board of each new game. */
    private final BoardGenerator generator;
    /** Used to choose the cell and the number of a hint at random. */
    private final Random random;
    /** Board of the current game. */
    private Board board;
    /** Hints the player can still use in the current game. */
    private int remainingHints;

    /** Creates a game with the default generator and a random choice. */
    public Game() {
        // Use the default generator
        this(new SudokuGenerator());
    }

    /** Creates a game with the given generator.
     * @param generator creates the board of each game
     */

    public Game(BoardGenerator generator) {
        // Use a new random source
        this(generator, new Random());
    }

    /** Creates a game with the given generator and random source. It is useful for tests, because a fixed random gives repeatable results.
     * @param generator creates the board of each game
     * @param random chooses the hints at random
     * @throws IllegalArgumentException if any of them is null
     */

    public Game(BoardGenerator generator, Random random) {
        // Both are needed to play, so null is not accepted
        if (generator == null || random == null) {
            throw new IllegalArgumentException("El generador y el aleatorio no pueden ser nulos.");
        }
        this.generator = generator;
        this.random = random;
        // Create the first board and give the hints
        startGame();
    }

    /** Creates a game that starts with a board that is already made (for example, in tests).
     * @param initialBoard the board to start with
     * @throws IllegalArgumentException if the board is null
     */

    public Game(Board initialBoard) {
        // Without a board there is no game
        if (initialBoard == null) {
            throw new IllegalArgumentException("El tablero inicial no puede ser nulo.");
        }
        // Default generator, used only if a new game is requested later
        this.generator = new SudokuGenerator();
        this.random = new Random();
        // Play on the board that was given
        this.board = initialBoard;
        this.remainingHints = HINTS_PER_GAME;
    }

    /** Starts a new game: creates a new board and gives all the hints again.
     * @return the new board
     */

    public Board newGame() {
        // Replace the board and reset the hints
        startGame();
        return board;
    }

    /** Returns the board of the current game.
     * @return the board
     */

    public Board getBoard() {
        return board;
    }

    /** Returns how many hints the player can still use.
     * @return the remaining hints
     */

    public int getRemainingHints() {
        return remainingHints;
    }

    /**
     * Puts a number in a cell and checks it. If the number repeats in its row,
     * column or block, it is still placed, but the cells in conflict are returned
     * so they can be highlighted.
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @param value  number to put (1 to 6)
     * @return the result of the move, with a message for the player
     */

    public MoveResult placeNumber(int row, int column, int value) {
        // Reject a cell outside the board
        if (!Board.isInsideBoard(row, column)) {
            return MoveResult.rejected("La posición está fuera del tablero.");
        }
        // Reject numbers that are not from 1 to 6
        if (value < Board.MIN_VALUE || value > Board.MAX_VALUE) {
            return MoveResult.rejected("Solo se permiten números del 1 al 6.");
        }
        // Reject changes to the initial clues
        if (board.isFixed(row, column)) {
            return MoveResult.rejected("Esa celda es una pista fija y no se puede modificar.");
        }
        // The move is allowed: place the number
        board.set(row, column, value);
        // Look for cells that repeat this number
        Set<Position> conflicts = SudokuRules.findConflictsOf(board, row, column);
        // No conflicts: the number follows the rules
        if (conflicts.isEmpty()) {
            // This was the last number: the game is won
            if (isSolved()) {
                // Solved = no empty cells and no repeated numbers
                return MoveResult.appliedValid("¡Tablero completado correctamente!");
            }
            return MoveResult.appliedValid("Número colocado.");
        }
        // Placed, but it repeats a number: send the cells to highlight
        return MoveResult.appliedWithConflicts(conflicts,
                "El número " + value + " incumple las reglas del Sudoku.");
    }

    /**
     * Empties a cell that the player filled before.
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @return the result of the move, with a message for the player
     */

    public MoveResult clearCell(int row, int column) {
        // Reject a cell outside the board
        if (!Board.isInsideBoard(row, column)) {
            return MoveResult.rejected("La posición está fuera del tablero.");
        }
        // Reject changes to the initial clues
        if (board.isFixed(row, column)) {
            return MoveResult.rejected("Esa celda es una pista fija y no se puede modificar.");
        }
        // Nothing to delete in an empty cell
        if (board.isEmpty(row, column)) {
            return MoveResult.rejected("La celda ya está vacía.");
        }
        // Empty the cell
        board.set(row, column, Board.EMPTY);
        return MoveResult.appliedValid("Número eliminado.");
    }

    /** Tells if the player solved the game: the board is full and has no repeated numbers.
     * @return true if the game is solved
     */

    public boolean isSolved() {
        return board.isFull() && SudokuRules.isValidBoard(board);
    }

    /** Returns all the cells that repeat a number in their row, column or block.
     * @return the cells in conflict (empty if none)
     */

    public Set<Position> getConflicts() {
        return SudokuRules.findConflicts(board);
    }

    /**
     * Gives a hint: a number that can be placed in an empty cell without breaking
     * the rules. It does not place the number; the player decides. Each hint uses
     * one of the limited hints, so the board cannot be finished using only hints.
     *
     * @return the hint, or null if there are no hints left or no cell can receive a number
     */

    public Hint requestHint() {
        // No hints left
        if (remainingHints <= 0) {
            return null;
        }
        // Collect all the empty cells
        List<Position> emptyCells = new ArrayList<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                if (board.isEmpty(row, column)) {
                    emptyCells.add(new Position(row, column));
                }
            }
        }
        // Mix them, so the hint is not always in the same place
        Collections.shuffle(emptyCells, random);
        // Look for the first empty cell that can receive a number
        for (Position position : emptyCells) {
            // Numbers that follow the rules in this cell
            List<Integer> candidates = SudokuRules.findCandidates(board,
                    position.getRow(), position.getColumn());
            // Found a cell with at least one possible number
            if (!candidates.isEmpty()) {
                // Choose one of the possible numbers at random
                int value = candidates.get(random.nextInt(candidates.size()));
                // Using a hint costs one
                remainingHints--;
                return new Hint(position, value, remainingHints);
            }
        }
        // No empty cell can receive a number
        return null;
    }

    /** Creates a new board with the generator and gives all the hints again. */
    private void startGame() {
        // Ask the generator for a new board
        this.board = generator.generate();
        // Reset the hints
        this.remainingHints = HINTS_PER_GAME;
    }
}
