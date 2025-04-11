package com.algotugas;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryScannerController {

    @FXML
    private TextField directoryPathField;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private Button browseButton;
    
    @FXML
    private Button scanButton;
    
    @FXML
    private TableView<FileInfo> resultsTable;
    
    @FXML
    private TableColumn<FileInfo, String> fileNameColumn;
    
    @FXML
    private TableColumn<FileInfo, String> fileSizeColumn;
    
    @FXML
    private TableColumn<FileInfo, String> filePathColumn;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label statusLabel;
    
    private int totalFiles = 0;
    private int matchingFiles = 0;
    private long totalSize = 0;

    @FXML
    private void initialize() {
        // Inisialisasi kolom tabel
        fileNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFileName()));
            
        fileSizeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFormattedSize()));
            
        filePathColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFilePath()));
        
        // Set event handler untuk tombol Browse
        browseButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Pilih Direktori untuk Scan");
            File selectedDirectory = directoryChooser.showDialog(App.stage);
            
            if (selectedDirectory != null) {
                directoryPathField.setText(selectedDirectory.getAbsolutePath());
            }
        });
        
        // Set event handler untuk tombol Scan
        scanButton.setOnAction(event -> {
            String directoryPath = directoryPathField.getText();
            String name = nameField.getText().trim();
            
            if (directoryPath.isEmpty()) {
                showAlert("Error", "Silakan pilih direktori terlebih dahulu!");
                return;
            }
            
            File directory = new File(directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                showAlert("Error", "Direktori yang dipilih tidak valid!");
                return;
            }
            
            // Reset nilai
            resultsTable.getItems().clear();
            totalFiles = 0;
            matchingFiles = 0;
            totalSize = 0;
            progressBar.setProgress(0);
            statusLabel.setText("Memindai...");
            
            // Lakukan pemindaian dalam thread terpisah
            Task<List<FileInfo>> scanTask = new Task<>() {
                @Override
                protected List<FileInfo> call() {
                    List<FileInfo> results = new ArrayList<>();
                    scanDirectory(directory, name, results);
                    return results;
                }
            };
            
            scanTask.setOnSucceeded(e -> {
                List<FileInfo> results = scanTask.getValue();
                resultsTable.getItems().addAll(results);
                progressBar.setProgress(1.0);
                statusLabel.setText(String.format(
                    "Scan selesai: %d file ditemukan dari %d file (Total: %s)",
                    matchingFiles, totalFiles, formatSize(totalSize)));
            });
            
            new Thread(scanTask).start();
        });
    }

    private void scanDirectory(File directory, String name, List<FileInfo> results) {
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    totalFiles++;
                    updateStatus();
                    
                    // Periksa apakah file memiliki ekstensi yang dicari
                    boolean matchesName = name.isEmpty() || 
                        file.getName().toLowerCase().contains(name.toLowerCase());
                    
                    if (matchesName) {
                        matchingFiles++;
                        totalSize += file.length();
                        
                        FileInfo fileInfo = new FileInfo(
                            file.getName(), 
                            file.length(),
                            file.getAbsolutePath()
                        );
                        
                        results.add(fileInfo);
                        
                        // Update UI setiap 10 file
                        if (matchingFiles % 10 == 0) {
                            final int current = matchingFiles;
                            Platform.runLater(() -> {
                                statusLabel.setText(String.format(
                                    "Memindai... %d file ditemukan dari %d file",
                                    current, totalFiles));
                            });
                        }
                    }
                } else if (file.isDirectory()) {
                    // Rekursi ke subdirektori
                    scanDirectory(file, name, results);
                }
            }
        }
    }
    
    /**
     * Metode rekursif alternatif untuk menghitung ukuran direktori
     */
    private long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    // Rekursi untuk subdirektori
                    size += calculateDirectorySize(file);
                }
            }
        }
        
        return size;
    }
    
    private void updateStatus() {
        final int current = totalFiles;
        // Update progress hanya setiap 20 file
        if (current % 20 == 0) {
            Platform.runLater(() -> {
                // Perkiraan progres berdasarkan jumlah file yang ditemukan
                double progress = Math.min(0.99, current / 1000.0);
                progressBar.setProgress(progress);
            });
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private String formatSize(long bytes) {
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size > 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    // Kelas untuk menyimpan informasi file
    public static class FileInfo {
        private final String fileName;
        private final long fileSize;
        private final String filePath;
        
        public FileInfo(String fileName, long fileSize, String filePath) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.filePath = filePath;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public long getFileSize() {
            return fileSize;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public String getFormattedSize() {
            final String[] units = {"B", "KB", "MB", "GB", "TB"};
            int unitIndex = 0;
            double size = fileSize;
            
            while (size > 1024 && unitIndex < units.length - 1) {
                size /= 1024;
                unitIndex++;
            }
            
            return String.format("%.2f %s", size, units[unitIndex]);
        }
    }
}
