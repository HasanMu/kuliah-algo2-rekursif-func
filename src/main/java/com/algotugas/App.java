package com.algotugas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        // Directory Scanner
        scene = new Scene(loadFXML("splash"), 640, 480);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("Recursive Directory Scanner - 24552011092 Hasan Muhammad Sholeh - TIFK24A");
        stage.setScene(scene);
        stage.show();

        // Fibonacci Sequence Visualization
        // scene = new Scene(loadFXML("splash"), 800, 600);
        // scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        // stage.setTitle("Fibonacci Sequence Visualization");
        // stage.setScene(scene);
        // stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}