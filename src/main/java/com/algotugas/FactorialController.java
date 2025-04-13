package com.algotugas;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class FactorialController {

    @FXML
    private TextField angka;

    @FXML
    private Button tombol;
    @FXML
    private Button halTextFiles;

    @FXML
    private Text hasil;

    @FXML
    private void initialize() {
        App.stage.setTitle("Factorial App");
        tombol.setOnAction(e -> {
            try {
                int number = Integer.parseInt(angka.getText());
                if (number < 0) {
                    hasil.setText("Bilangan harus tidak negatif!");
                } else {
                    hasil.setText("Hasil: " + factorial(number));
                }
            } catch (NumberFormatException ex) {
                hasil.setText("Masukkan angka yang valid!");
            }
        });

        halTextFiles.setOnAction(e -> {
            try {
                App.setRoot("text-files");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private long factorial(int n) {
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}