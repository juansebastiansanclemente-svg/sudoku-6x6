package sudoku.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sudoku.model.Tablero;

public class SudokuController {

    @FXML
    private GridPane tableroGrid;

    @FXML
    public void initialize() {
        construirTablero();

    }
    private void construirTablero() {
        for (int fila = 0; fila <Tablero.TAMANO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANO; columna++) {
                TextField celda = new TextField();
                tableroGrid.add(celda, columna, fila);
            }
        }
    }
}

