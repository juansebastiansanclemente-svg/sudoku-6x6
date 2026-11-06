package sudoku.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import sudoku.model.*;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Controller of the Sudoku window. It receives the events of the player (keyboard,
 * mouse and buttons), asks the game what to do, and updates what the window shows.
 * It has no Sudoku rules, those are in the model.
 */

public class SudokuController {

    /** Grid that holds the 36 cells of the board. */
    @FXML
    private GridPane boardGrid;

    /** Text at the bottom of the window, used to give messages to the player. */
    @FXML
    private Label messageLabel;

    /** Button that starts a new game. */
    @FXML
    private Button newGameButton;

    /** Button that asks for a hint. */
    @FXML
    private Button helpButton;
    /** Text fields of the board, one per cell: celdas[row][column]. */
    private final TextField[][] cells = new TextField[sudoku.model.Board.SIZE][sudoku.model.Board.SIZE];
    /** The current game: the board, the moves and the hints (the model). */
    private final Game game = new Game();

    /** Runs when the window opens: draws the board and connects the buttons. */
    @FXML
    public void initialize() {
        buildBoard();
        // The game starts with a board ready, so tell the player what to do
        messageLabel.setText("Selecciona una celda para empezar.");
        // Connect the new game button to its action.
        newGameButton.setOnAction(evento -> pedirConfirmacionNuevoJuego());
        // Connect the hint button to its action
        helpButton.setOnAction(event -> showHelp());
    }

    /**
     * Draws the board: creates one text field for each cell and gives it its style,
     * its keyboard and mouse events, and the lines that separate the blocks.
     */
    private void buildBoard() {
        // Board of the current game
        Board board = game.getBoard();
        // Go through the 36 cells of the board
        for (int row = 0; row < sudoku.model.Board.SIZE; row++) {
            for (int column = 0; column < sudoku.model.Board.SIZE; column++) {
                // Create the text field of this cell
                TextField cell = new TextField();
                // Keep a reference, to update this cell later (errors, hints, new game)
                cells[row][column] = cell;
                // Size of the cell and centered text
                cell.setPrefSize(60, 60);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
                // Show the number if the cell is not empty
                if (!board.isEmpty(row, column)) {
                    cell.setText(String.valueOf(board.get(row, column)));
                }
                // Initial clues cannot be edited and have their own style
                if (board.isFixed(row, column)) {
                    cell.setEditable(false);
                    cell.getStyleClass().add("fixed-cell");
                }
                // Every cell reacts to the keyboard (fixed cells too, so the arrows can pass over them)
                final int cellRow = row;
                final int cellColumn = column;
                cell.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKey(cellRow, cellColumn, event));
                // Block the typed character too, so the text field does not write by itself
                cell.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
                // Thick line on the right of each block (except the last one)
                if ((column + 1) % sudoku.model.Board.COLUMNS_PER_BLOCK == 0 && column != sudoku.model.Board.SIZE - 1) {
                    cell.getStyleClass().add("block-right-border");
                }
                // Mouse: a click selects the sudoku cell
                cell.setOnMouseClicked(event -> selectCell(cellRow, cellColumn));
                // Thick line under each block (except the last one)
                if ((row + 1) % sudoku.model.Board.ROWS_PER_BLOCK == 0 && row != sudoku.model.Board.SIZE - 1) {
                    cell.getStyleClass().add("block-bottom-border");
                }
                // Put the cell in the grid at its column and row
                boardGrid.add(cell, column, row);

            }
        }
    }

    /**
     * Handles a key pressed in a cell: arrows move to the neighbour cell, a number
     * from 1 to 6 is written, Backspace or Delete empties the cell, and any other
     * key is ignored.
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @param event  the key event
     */
    private void handleKey(int row, int column, KeyEvent event) {
        TextField cell = cells[row][column];
        KeyCode keyCode = event.getCode();

        // Stop the default typing: only the game decides what the cell shows (Tab still moves between cells)
        if (keyCode != KeyCode.TAB) {
            event.consume();
        }

        // Text of the key pressed (for example 3)
        String text = event.getText();

        if (keyCode == KeyCode.UP) {
            moveTo(row - 1, column);
        } else if (keyCode == KeyCode.DOWN) {
            moveTo(row + 1, column);
        } else if (keyCode == KeyCode.LEFT) {
            moveTo(row, column - 1);
        } else if (keyCode == KeyCode.RIGHT) {
            moveTo(row, column + 1);
        } else if (text.length() == 1 && text.charAt(0) >= '1' && text.charAt(0) <= '6') {
            // A number from 1 to 6, ask the game to place it
            int number = text.charAt(0) - '0';
            MovementResult result = game.makeMove(row, column, number);
            // Show the message of the game (error or confirmation)
            messageLabel.setText(result.getMessage());
            if (result.isApplied()) {
                cell.setText(String.valueOf(number));
                updateMistakes();
                clearHelp();
            }
        } else if (keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE) {
            // Backspace or Delete: ask the game to empty the cell
            MovementResult result = game.erase(row, column);
            messageLabel.setText(result.getMessage());
            if (result.isApplied()) {
                cell.clear();
                updateMistakes();
                clearHelp();
            }
        }
    }
    /**
     * Moves the keyboard focus to another cell, if that cell is inside the board.
     *
     * @param row    row of the cell to go to
     * @param column column of the cell to go to
     */
    private void moveTo(int row, int column) {
        // At the edge of the board there is no neighbour: do nothing
        if (sudoku.model.Board.isOnBoard(row, column)) {
            cells[row][column].requestFocus();
        }
    }
    /**
     * Selects a cell when the player clicks on it: the cell takes the keyboard
     * focus and the message tells which cell it is.
     *
     * @param row    row of the cell
     * @param column column of the cell
     */
    private void selectCell(int row, int column) {
        // Give the keyboard to this cell, so the next key goes there
        cells[row][column].requestFocus();

        // Rows and columns are shown from 1 for the player
        String position = "fila " + (row + 1) + ", columna " + (column + 1);

        if (game.getBoard().isFixed(row, column)) {
            messageLabel.setText("Celda (" + position + "): es una pista fija.");
        } else {
            messageLabel.setText("Celda seleccionada: " + position + ".");
        }
    }

    /**
     * Marks in red the cells that repeat a number in their row, column or block,
     * and removes the mark from the rest.
     */
    private void updateMistakes() {
        // Remove the old marks from every cell
        for (int row = 0; row < sudoku.model.Board.SIZE; row++) {
            for (int column = 0; column < sudoku.model.Board.SIZE; column++) {
                cells[row][column].getStyleClass().remove("cell-mistake");
            }
        }

        // Mark the cells that are in conflict right now
        for (Position position : game.getConflicts()) {
            cells[position.getRow()][position.getColumn()].getStyleClass().add("cell-mistake");
        }
    }
    /**
     * Gives a hint to the player: highlights an empty cell and tells which number
     * can be placed there. The number is not written; the player decides.
     */
    private void showHelp() {
        // Remove the previous hint, if any
        clearHelp();

        Hint hint = game.getHint();

        if (hint == null) {
            // No hint was given: tell the player why
            if (game.getRemainingHints() <= 0) {
                messageLabel.setText("Ya usaste todas las ayudas.");
            } else {
                messageLabel.setText("No hay ninguna ayuda disponible en este momento.");
            }
            return;
        }

        Position position = hint.getPosition();
        // Highlight the suggested cell
        cells[position.getRow()][position.getColumn()].getStyleClass().add("cell-hint");
        // Tell the number (rows and columns are shown from 1 for the player)
        messageLabel.setText("Ayuda: prueba con el " + hint.getValue()
                + " en la fila " + (position.getRow() + 1)
                + ", columna " + (position.getColumn() + 1)
                + ". Ayudas restantes: " + hint.remainingHint());
    }

    /** Removes the hint highlight from every cell. */
    private void clearHelp() {
        for (int row = 0; row < sudoku.model.Board.SIZE; row++) {
            for (int column = 0; column < sudoku.model.Board.SIZE; column++) {
                cells[row][column].getStyleClass().remove("cell-hint");
            }
        }
    }

    /**
     * Asks the player to confirm before starting a new game. If the player accepts,
     * a new board is created and drawn.
     */
    private void pedirConfirmacionNuevoJuego() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nueva partida");
        alert.setHeaderText("¿Quieres iniciar una nueva partida?");
        alert.setContentText("Se perderá el progreso de la partida actual.");

        // Wait for the answer of the player
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Accepted: create a new board and draw it again
            game.startNewGame();
            boardGrid.getChildren().clear();
            buildBoard();
            messageLabel.setText("Nueva partida iniciada.");
        }
    }
}