package sudoku;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Entry point of the application. It loads the Sudoku window from its FXML file,
 * applies the style sheet and shows it.
 */
public class Main extends Application {

    /**
     * Builds and shows the main window.
     *
     * @param stage the main window given by JavaFX
     * @throws Exception if the FXML file or the style sheet cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Prepare the loader with the file that describes the window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudoku/sudoku-view.fxml"));
        // Build the window; this also creates the controller
        Parent root = loader.load();

        // The scene holds everything the window shows
        Scene scene = new Scene(root);
        // Apply the colors and styles of the CSS file
        scene.getStylesheets().add(getClass().getResource("/sudoku/style.css").toExternalForm());
        // Window title, scene and show it
        stage.setTitle("Sudoku 6x6");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Starts JavaFX, which then calls start()
        launch();
    }
}