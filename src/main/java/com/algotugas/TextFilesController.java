package com.algotugas;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextFilesController {
    private static final String NAMA_FILE = "Hasan Muhammad Sholeh_24552011092.txt";

    @FXML
    private TextField inputNama;
    
    @FXML
    private TextField inputNim;
    
    @FXML
    private Button tombolSimpan;
    
    @FXML
    private Button tombolBaca;
    
    @FXML
    private Button tombolUbah;
    
    @FXML
    private Button tombolKembali;
    
    @FXML
    private TextArea areaTeks;

    @FXML
    private void initialize() {
        App.stage.setTitle("Text Files App");
        
        // Tombol Simpan
        tombolSimpan.setOnAction(e -> {
            try (FileWriter writer = new FileWriter(NAMA_FILE, true)) {
                String data = "Nama: " + inputNama.getText() + ", NIM: "+ inputNim.getText() +"\n";
                writer.write(data);
                inputNama.clear();
                inputNim.clear();
                areaTeks.setText("Berhasil menyimpan data.");
            } catch (IOException ex) {
                areaTeks.setText("Gagal menyimpan data.");
            }
        });

        // Tombol Baca
        tombolBaca.setOnAction(e -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(NAMA_FILE))) {
                StringBuilder isi = new StringBuilder();
                String baris;
                while ((baris = reader.readLine()) != null) {
                    isi.append(baris).append("\n");
                }
                areaTeks.setText(isi.toString());
            } catch (IOException ex) {
                areaTeks.setText("Gagal membaca file.");
            }
        });

        // Tombol Ubah
        tombolUbah.setOnAction(e -> {
            String namaInput = inputNama.getText();
            String nimInput = inputNim.getText();

            List<String> isiBaru = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(NAMA_FILE))) {
                String baris;
                boolean ditemukan = false;
                while ((baris = reader.readLine()) != null) {
                    if (baris.contains("Nama: " + namaInput + ",")) {
                        // Ubah pesan untuk nama tersebut
                        baris = "Nama: " + namaInput + ", NIM: "+ nimInput;
                        ditemukan = true;
                    }
                    if (baris.contains("NIM: " + nimInput + ",")) {
                        // Ubah pesan untuk NIM tersebut
                        baris = "Nama: " + namaInput + ", NIM: "+ nimInput;
                        ditemukan = true;
                    }
                    isiBaru.add(baris);
                }

                if (!ditemukan) {
                    areaTeks.setText("Data dengan nama tersebut tidak ditemukan.");
                    return;
                }

            } catch (IOException ex) {
                areaTeks.setText("Gagal membaca file.");
                return;
            }

            // Tulis ulang file dengan data yang sudah diperbarui
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(NAMA_FILE, false))) {
                for (String baris : isiBaru) {
                    writer.write(baris);
                    writer.newLine();
                }
                areaTeks.setText("Data berhasil diubah.");
                inputNama.clear();
                inputNim.clear();
            } catch (IOException ex) {
                areaTeks.setText("Gagal menyimpan perubahan.");
            }
        });
        
        // Tombol Kembali
        tombolKembali.setOnAction(e -> {
            try {
                App.setRoot("factorial");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
