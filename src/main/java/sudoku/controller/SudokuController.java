package sudoku.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import sudoku.model.Game;
import sudoku.model.Position;
import sudoku.model.MoveResult;
import sudoku.model.Board;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import sudoku.model.Hint;

/**
 * Controller of the Sudoku window. It receives the events of the player (keyboard,
 * mouse and buttons), asks the game what to do, and updates what the window shows.
 * It has no Sudoku rules, those are in the model.
 */

public class SudokuController {

    /** Grid that holds the 36 cells of the board. */
    @FXML
    private GridPane tableroGrid;

    /** Text at the bottom of the window, used to give messages to the player. */
    @FXML
    private Label mensajeLabel;

    /** Button that starts a new game. */
    @FXML
    private Button nuevoJuegoButton;

    /** Button that asks for a hint. */
    @FXML
    private Button ayudaButton;
    /** Text fields of the board, one per cell: celdas[row][column]. */
    private final TextField[][] celdas = new TextField[Board.SIZE][Board.SIZE];
    /** The current game: the board, the moves and the hints (the model). */
    private final Game partida = new Game();

    /** Runs when the window opens: draws the board and connects the buttons. */
    @FXML
    public void initialize() {
        construirTablero();
        // The game starts with a board ready, so tell the player what to do
        mensajeLabel.setText("Selecciona una celda para empezar.");
        // Connect the new game button to its action.
        nuevoJuegoButton.setOnAction(evento -> pedirConfirmacionNuevoJuego());
        // Connect the hint button to its action
        ayudaButton.setOnAction(event -> mostrarAyuda());
    }

    /**
     * Draws the board: creates one text field for each cell and gives it its style,
     * its keyboard and mouse events, and the lines that separate the blocks.
     */
    private void construirTablero() {
        // Board of the current game
        Board tablero = partida.getBoard();
        // Go through the 36 cells of the board
        for (int fila = 0; fila < Board.SIZE; fila++) {
            for (int columna = 0; columna < Board.SIZE; columna++) {
                // Create the text field of this cell
                TextField celda = new TextField();
                // Keep a reference, to update this cell later (errors, hints, new game)
                celdas[fila][columna] = celda;
                // Size of the cell and centered text
                celda.setPrefSize(60, 60);
                celda.setAlignment(Pos.CENTER);
                // Show the number if the cell is not empty
                if (!tablero.isEmpty(fila, columna)) {
                    celda.setText(String.valueOf(tablero.get(fila, columna)));
                }
                // Initial clues cannot be edited and have their own style
                if (tablero.isFixed(fila, columna)) {
                    celda.setEditable(false);
                    celda.getStyleClass().add("celda-fija");
                }
                // Every cell reacts to the keyboard (fixed cells too, so the arrows can pass over them)
                final int filaCelda = fila;
                final int columnaCelda = columna;
                celda.addEventFilter(KeyEvent.KEY_PRESSED, evento -> manejarTecla(filaCelda, columnaCelda, evento));
                // Block the typed character too, so the text field does not write by itself
                celda.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
                // Thick line on the right of each block (except the last one)
                if ((columna + 1) % Board.BLOCK_COLUMNS == 0 && columna != Board.SIZE - 1) {
                    celda.getStyleClass().add("borde-derecho-bloque");
                }
                // Mouse: a click selects the sudoku cell
                celda.setOnMouseClicked(event -> seleccionarCelda(filaCelda, columnaCelda));
                // Thick line under each block (except the last one)
                if ((fila + 1) % Board.BLOCK_ROWS == 0 && fila != Board.SIZE - 1) {
                    celda.getStyleClass().add("borde-inferior-bloque");
                }
                // Put the cell in the grid at its column and row
                tableroGrid.add(celda, columna, fila);

            }
        }
    }

    /**
     * Handles a key pressed in a cell: arrows move to the neighbour cell, a number
     * from 1 to 6 is written, Backspace or Delete empties the cell, and any other
     * key is ignored.
     *
     * @param fila    row of the cell
     * @param columna column of the cell
     * @param evento  the key event
     */
    private void manejarTecla(int fila, int columna, KeyEvent evento) {
        TextField celda = celdas[fila][columna];
        KeyCode tecla = evento.getCode();

        // Stop the default typing: only the game decides what the cell shows (Tab still moves between cells)
        if (tecla != KeyCode.TAB) {
            evento.consume();
        }

        // Text of the key pressed (for example 3)
        String texto = evento.getText();

        if (tecla == KeyCode.UP) {
            moverA(fila - 1, columna);
        } else if (tecla == KeyCode.DOWN) {
            moverA(fila + 1, columna);
        } else if (tecla == KeyCode.LEFT) {
            moverA(fila, columna - 1);
        } else if (tecla == KeyCode.RIGHT) {
            moverA(fila, columna + 1);
        } else if (texto.length() == 1 && texto.charAt(0) >= '1' && texto.charAt(0) <= '6') {
            // A number from 1 to 6, ask the game to place it
            int numero = texto.charAt(0) - '0';
            MoveResult resultado = partida.placeNumber(fila, columna, numero);
            // Show the message of the game (error or confirmation)
            mensajeLabel.setText(resultado.getMessage());
            if (resultado.isApplied()) {
                celda.setText(String.valueOf(numero));
                actualizarErrores();
                limpiarAyuda();
            }
        } else if (tecla == KeyCode.BACK_SPACE || tecla == KeyCode.DELETE) {
            // Backspace or Delete: ask the game to empty the cell
            MoveResult resultado = partida.clearCell(fila, columna);
            mensajeLabel.setText(resultado.getMessage());
            if (resultado.isApplied()) {
                celda.clear();
                actualizarErrores();
                limpiarAyuda();
            }
        }
    }
    /**
     * Moves the keyboard focus to another cell, if that cell is inside the board.
     *
     * @param fila    row of the cell to go to
     * @param columna column of the cell to go to
     */
    private void moverA(int fila, int columna) {
        // At the edge of the board there is no neighbour: do nothing
        if (Board.isInsideBoard(fila, columna)) {
            celdas[fila][columna].requestFocus();
        }
    }
    /**
     * Selects a cell when the player clicks on it: the cell takes the keyboard
     * focus and the message tells which cell it is.
     *
     * @param fila    row of the cell
     * @param columna column of the cell
     */
    private void seleccionarCelda(int fila, int columna) {
        // Give the keyboard to this cell, so the next key goes there
        celdas[fila][columna].requestFocus();

        // Rows and columns are shown from 1 for the player
        String lugar = "fila " + (fila + 1) + ", columna " + (columna + 1);

        if (partida.getBoard().isFixed(fila, columna)) {
            mensajeLabel.setText("Celda (" + lugar + "): es una pista fija.");
        } else {
            mensajeLabel.setText("Celda seleccionada: " + lugar + ".");
        }
    }

    /**
     * Marks in red the cells that repeat a number in their row, column or block,
     * and removes the mark from the rest.
     */
    private void actualizarErrores() {
        // Remove the old marks from every cell
        for (int fila = 0; fila < Board.SIZE; fila++) {
            for (int columna = 0; columna < Board.SIZE; columna++) {
                celdas[fila][columna].getStyleClass().remove("celda-error");
            }
        }

        // Mark the cells that are in conflict right now
        for (Position posicion : partida.getConflicts()) {
            celdas[posicion.getRow()][posicion.getColumn()].getStyleClass().add("celda-error");
        }
    }
    /**
     * Gives a hint to the player: highlights an empty cell and tells which number
     * can be placed there. The number is not written; the player decides.
     */
    private void mostrarAyuda() {
        // Remove the previous hint, if any
        limpiarAyuda();

        Hint sugerencia = partida.requestHint();

        if (sugerencia == null) {
            // No hint was given: tell the player why
            if (partida.getRemainingHints() <= 0) {
                mensajeLabel.setText("Ya usaste todas las ayudas.");
            } else {
                mensajeLabel.setText("No hay ninguna ayuda disponible en este momento.");
            }
            return;
        }

        Position posicion = sugerencia.getPosition();
        // Highlight the suggested cell
        celdas[posicion.getRow()][posicion.getColumn()].getStyleClass().add("celda-ayuda");
        // Tell the number (rows and columns are shown from 1 for the player)
        mensajeLabel.setText("Ayuda: prueba con el " + sugerencia.getValue()
                + " en la fila " + (posicion.getRow() + 1)
                + ", columna " + (posicion.getColumn() + 1)
                + ". Ayudas restantes: " + sugerencia.getRemainingHints());
    }

    /** Removes the hint highlight from every cell. */
    private void limpiarAyuda() {
        for (int fila = 0; fila < Board.SIZE; fila++) {
            for (int columna = 0; columna < Board.SIZE; columna++) {
                celdas[fila][columna].getStyleClass().remove("celda-ayuda");
            }
        }
    }

    /**
     * Asks the player to confirm before starting a new game. If the player accepts,
     * a new board is created and drawn.
     */
    private void pedirConfirmacionNuevoJuego() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Nueva partida");
        alerta.setHeaderText("¿Quieres iniciar una nueva partida?");
        alerta.setContentText("Se perderá el progreso de la partida actual.");

        // Wait for the answer of the player
        Optional<ButtonType> respuesta = alerta.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            // Accepted: create a new board and draw it again
            partida.newGame();
            tableroGrid.getChildren().clear();
            construirTablero();
            mensajeLabel.setText("Nueva partida iniciada.");
        }
    }
}