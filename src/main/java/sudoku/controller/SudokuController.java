package sudoku.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import sudoku.model.Partida;
import sudoku.model.Posicion;
import sudoku.model.ResultadoMovimiento;
import sudoku.model.Tablero;
import sudoku.model.Posicion;

public class SudokuController {

    @FXML
    private GridPane tableroGrid;

    /** Text fields of the board, one per cell: celdas[row][column]. */
    private final TextField[][] celdas = new TextField[Tablero.TAMANO][Tablero.TAMANO];

    private final Partida partida = new Partida();

    @FXML
    public void initialize() {
        construirTablero();
    }

    private void construirTablero() {
        Tablero tablero = partida.getTablero();
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                TextField celda = new TextField();
                // Keep a reference, to update this cell later (errors, hints, new game)
                celdas[fila][columna] = celda;
                celda.setPrefSize(60, 60);
                celda.setAlignment(Pos.CENTER);
                if (!tablero.estaVacia(fila, columna)) {
                    celda.setText(String.valueOf(tablero.get(fila, columna)));
                }
                if (tablero.esFija(fila, columna)) {
                    celda.setEditable(false);
                    celda.getStyleClass().add("celda-fija");
                } else {
                    // Only editable cells react to the keyboard
                    final int filaCelda = fila;
                    final int columnaCelda = columna;
                    celda.setOnKeyPressed(evento -> manejarTecla(filaCelda, columnaCelda, evento));
                    //Block the typed character too, so the text field does not write by itself
                    celda.addEventFilter(KeyEvent.KEY_TYPED, KeyEvent::consume);
                }
                if ((columna + 1) % Tablero.COLUMNAS_POR_BLOQUE == 0 && columna != Tablero.TAMANO - 1) {
                    celda.getStyleClass().add("borde-derecho-bloque");
                }
                if ((fila + 1) % Tablero.FILAS_POR_BLOQUE == 0 && fila != Tablero.TAMANO - 1) {
                    celda.getStyleClass().add("borde-inferior-bloque");
                }
                tableroGrid.add(celda, columna, fila);

            }
        }
    }

    /**
     * Handles a key pressed in a cell: a number from 1 to 6 is written,
     * Backspace or Delete empties the cell, and any other key is ignored.
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

        if (texto.length() == 1 && texto.charAt(0) >= '1' && texto.charAt(0) <= '6') {
            // A number from 1 to 6, ask the game to place it
            int numero = texto.charAt(0) - '0';
            ResultadoMovimiento resultado = partida.ingresar(fila, columna, numero);
            if (resultado.isAplicado()) {
                celda.setText(String.valueOf(numero));
                actualizarErrores();
            }
        } else if (tecla == KeyCode.BACK_SPACE || tecla == KeyCode.DELETE) {
            // Backspace or Delete: ask the game to empty the cell
            ResultadoMovimiento resultado = partida.borrar(fila, columna);
            if (resultado.isAplicado()) {
                celda.clear();
                actualizarErrores();
            }
        }
    }
    /**
     * Marks in red the cells that repeat a number in their row, column or block,
     * and removes the mark from the rest.
     */
    private void actualizarErrores() {
        //Remove the old marks from every cell
        for (int fila = 0; fila < Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                celdas[fila][columna].getStyleClass().remove("celda-error");
            }
        }
        for (Posicion posicion : partida.getConflictos()){
            celdas[posicion.getFila()][posicion.getColumna()].getStyleClass().add("celda-error");
        }
    }
}