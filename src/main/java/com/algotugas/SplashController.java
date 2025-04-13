package com.algotugas;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class SplashController {
    
    @FXML
    private ProgressBar progressBar;

    private static final double LOADING_TIME_SECONDS = 4.0;

    @FXML
    public void initialize() {
        // Animasi progress bar dari 0 ke 1 selama 4 detik
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(LOADING_TIME_SECONDS), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.play();
        
        // Buat delay 4 detik
        PauseTransition delay = new PauseTransition(Duration.seconds(LOADING_TIME_SECONDS));
        delay.setOnFinished(event -> switchToPrimary());
        delay.play();
    }
    
    private void switchToPrimary() {
        try {
            // Dapatkan root node saat ini
            Parent currentRoot = App.stage.getScene().getRoot();
            
            // Buat fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            // Setelah fade out selesai, ganti ke primary view
            fadeOut.setOnFinished(event -> {
                try {
                    // App.setRoot("directory-scanner");
                    App.setRoot("factorial");
                    
                    // Buat fade in animation untuk root baru
                    Parent newRoot = App.stage.getScene().getRoot();
                    newRoot.setOpacity(0); // Mulai dengan opacity 0
                    
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), newRoot);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
