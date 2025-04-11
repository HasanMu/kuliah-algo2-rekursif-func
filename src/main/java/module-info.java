module com.algotugas {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    exports com.algotugas;
    opens com.algotugas to javafx.fxml;
}