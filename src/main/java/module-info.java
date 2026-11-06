/** Sudoku 6x6 game made with JavaFX, following the MVC pattern. */
module sudoku {
    requires javafx.controls;
    requires javafx.fxml;

    opens sudoku.controller to javafx.fxml;
    exports sudoku;
}