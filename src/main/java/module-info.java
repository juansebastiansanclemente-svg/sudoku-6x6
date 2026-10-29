module sudoku {
    requires javafx.controls;
    requires javafx.fxml;

    opens sudoku.controller to javafx.fxml;
    exports sudoku;
}