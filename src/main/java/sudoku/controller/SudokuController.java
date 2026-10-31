package sudoku.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sudoku.model.Tablero;
import javafx.geometry.Pos;

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
                celda.setPrefSize(60, 60);
                celda.setAlignment(Pos.CENTER);
                if ((columna + 1) % Tablero.COLUMNAS_POR_BLOQUE == 0 && columna != Tablero.TAMANO - 1){
                    celda.getStyleClass().add("borde-derecho-bloque");
                    }
                if ((fila + 1) % Tablero.FILAS_POR_BLOQUE == 0 && fila != Tablero.TAMANO - 1){
                    celda.getStyleClass().add("borde-inferior-bloque");
                }
                tableroGrid.add(celda, columna, fila);
            }
        }
    }
}

