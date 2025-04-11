package com.algotugas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class FibonacciViewController {

    @FXML
    private TextField numberInput;

    @FXML
    private Button calculateButton;
    
    @FXML
    private Button animateButton;
    
    @FXML
    private Button resetButton;
    
    @FXML
    private Slider speedSlider;
    
    @FXML
    private Pane visualizationPane;
    
    @FXML
    private VBox resultContainer;
    
    @FXML
    private Text formulaText;
    
    @FXML
    private Label stepsLabel;
    
    private List<Long> fibonacciNumbers;
    private int recursionCalls = 0;
    private Timeline animation;
    private Canvas canvas;
    
    @FXML
    private void initialize() {
        // Create canvas for visualization
        canvas = new Canvas(600, 400);
        visualizationPane.getChildren().add(canvas);
        
        // Make canvas resize with the pane
        canvas.widthProperty().bind(visualizationPane.widthProperty());
        canvas.heightProperty().bind(visualizationPane.heightProperty());
        
        // Redraw when canvas size changes
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (fibonacciNumbers != null && !fibonacciNumbers.isEmpty()) {
                drawFibonacciSquares(fibonacciNumbers);
            }
        });
        
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (fibonacciNumbers != null && !fibonacciNumbers.isEmpty()) {
                drawFibonacciSquares(fibonacciNumbers);
            }
        });
        
        // Set event handler for calculate button
        calculateButton.setOnAction(event -> {
            calculateFibonacci();
        });
        
        // Set event handler for animate button
        animateButton.setOnAction(event -> {
            animateFibonacci();
        });
        
        // Set event handler for reset button
        resetButton.setOnAction(event -> {
            resetVisualization();
        });
        
        // Formula display
        formulaText.setText("F(n) = F(n-1) + F(n-2), where F(0) = 0 and F(1) = 1");
    }
    
    private void calculateFibonacci() {
        try {
            int n = Integer.parseInt(numberInput.getText().trim());
            
            if (n < 3 || n > 20) {
                showAlert("Input Error", "Please enter a number between 3 and 20 for visualization.");
                return;
            }
            
            // Reset visualizations
            resetVisualization();
            
            // Calculate Fibonacci sequence
            fibonacciNumbers = new ArrayList<>();
            recursionCalls = 0;
            
            // Calculate all Fibonacci numbers up to n
            for (int i = 0; i <= n; i++) {
                long fib = fibonacciRecursive(i);
                fibonacciNumbers.add(fib);
                
                // Display result in text form
                HBox resultRow = new HBox(10);
                Label indexLabel = new Label("F(" + i + ") = ");
                indexLabel.getStyleClass().add("result-label");
                
                Label valueLabel = new Label(String.valueOf(fib));
                valueLabel.getStyleClass().add("result-value");
                
                resultRow.getChildren().addAll(indexLabel, valueLabel);
                resultContainer.getChildren().add(resultRow);
            }
            
            // Draw Fibonacci squares
            drawFibonacciSquares(fibonacciNumbers);
            
            // Display recursion steps
            stepsLabel.setText("Recursive calls: " + recursionCalls);
            
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid integer.");
        }
    }
    
    private void animateFibonacci() {
        try {
            int n = Integer.parseInt(numberInput.getText().trim());
            
            if (n < 3 || n > 20) {
                showAlert("Input Error", "Please enter a number between 3 and 20 for visualization.");
                return;
            }
            
            // Reset visualizations
            resetVisualization();
            
            // Calculate Fibonacci sequence (we store but don't visualize yet)
            fibonacciNumbers = new ArrayList<>();
            recursionCalls = 0;
            
            for (int i = 0; i <= n; i++) {
                fibonacciNumbers.add(fibonacciRecursive(i));
            }
            
            // Set up animation
            animation = new Timeline();
            double duration = 1000 / speedSlider.getValue(); // Adjust speed based on slider
            
            for (int i = 1; i <= n; i++) {
                final int index = i;
                KeyFrame keyFrame = new KeyFrame(Duration.millis(duration * i), event -> {
                    // Display result text
                    HBox resultRow = new HBox(10);
                    Label indexLabel = new Label("F(" + index + ") = ");
                    indexLabel.getStyleClass().add("result-label");
                    
                    Label valueLabel = new Label(String.valueOf(fibonacciNumbers.get(index)));
                    valueLabel.getStyleClass().add("result-value");
                    
                    resultRow.getChildren().addAll(indexLabel, valueLabel);
                    resultContainer.getChildren().add(resultRow);
                    
                    // Draw squares up to this point
                    drawFibonacciSquaresAnimated(fibonacciNumbers.subList(0, index + 1));
                    
                    // Update steps label
                    if (index == n) {
                        stepsLabel.setText("Recursive calls: " + recursionCalls);
                    }
                });
                
                animation.getKeyFrames().add(keyFrame);
            }
            
            animation.play();
            
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid integer.");
        }
    }    
    private void drawFibonacciSquares(List<Long> numbers) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Skip F(0) since it's 0
        if (numbers.size() <= 1) return;
        
        // Scale factor to fit the visualization
        double maxFib = numbers.get(numbers.size() - 1);
        double scale = Math.min(canvas.getWidth(), canvas.getHeight()) * 0.8 / maxFib;
        
        // Center of the canvas
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        // Colors for the squares
        Color[] colors = {
            Color.rgb(255, 102, 102), // Red
            Color.rgb(255, 178, 102), // Orange
            Color.rgb(255, 255, 102), // Yellow
            Color.rgb(178, 255, 102), // Light green
            Color.rgb(102, 255, 102), // Green
            Color.rgb(102, 255, 178), // Teal
            Color.rgb(102, 255, 255), // Cyan
            Color.rgb(102, 178, 255), // Light blue
            Color.rgb(102, 102, 255), // Blue
            Color.rgb(178, 102, 255), // Purple
            Color.rgb(255, 102, 255)  // Pink
        };
        
        // Starting position and direction
        double x = centerX - (numbers.get(1) * scale / 2);
        double y = centerY - (numbers.get(1) * scale / 2);
        int direction = 0; // 0: right, 1: down, 2: left, 3: up
        
        // Draw squares for each Fibonacci number (skip F(0))
        for (int i = 1; i < numbers.size(); i++) {
            long size = numbers.get(i);
            double squareSize = size * scale;
            
            // Set color with cycling through the color array
            gc.setFill(colors[i % colors.length]);
            gc.fillRect(x, y, squareSize, squareSize);
            
            // Draw border
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeRect(x, y, squareSize, squareSize);
            
            // Add number label
            gc.setFill(Color.BLACK);
            gc.fillText("F(" + i + ") = " + size, x + squareSize/2 - 20, y + squareSize/2);
            
            // Update position for next square based on direction
            if (i + 1 < numbers.size()) {
                switch (direction) {
                    case 0: // right
                        x += squareSize;
                        y -= numbers.get(i+1) * scale;
                        break;
                    case 1: // down
                        y += squareSize;
                        x -= numbers.get(i+1) * scale;
                        break;
                    case 2: // left
                        x -= numbers.get(i+1) * scale;
                        break;
                    case 3: // up
                        y -= numbers.get(i+1) * scale;
                        break;
                }
                
                // Update direction (clockwise)
                direction = (direction + 1) % 4;
            }
        }
        
        // Draw spiral
        drawFibonacciSpiral(numbers, scale, centerX, centerY);
    }

    private void drawFibonacciSquaresAnimated(List<Long> numbers) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Skip F(0) since it's 0
        if (numbers.size() <= 1) return;
        
        // Scale factor to fit the visualization
        double maxFib = fibonacciNumbers.get(fibonacciNumbers.size() - 1); // Use full sequence for scaling
        double scale = Math.min(canvas.getWidth(), canvas.getHeight()) * 0.8 / maxFib;
        
        // Center of the canvas
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        // Colors for the squares
        Color[] colors = {
            Color.rgb(255, 102, 102), // Red
            Color.rgb(255, 178, 102), // Orange
            Color.rgb(255, 255, 102), // Yellow
            Color.rgb(178, 255, 102), // Light green
            Color.rgb(102, 255, 102), // Green
            Color.rgb(102, 255, 178), // Teal
            Color.rgb(102, 255, 255), // Cyan
            Color.rgb(102, 178, 255), // Light blue
            Color.rgb(102, 102, 255), // Blue
            Color.rgb(178, 102, 255), // Purple
            Color.rgb(255, 102, 255)  // Pink
        };
        
        // Starting position and direction
        double x = centerX - (numbers.get(1) * scale / 2);
        double y = centerY - (numbers.get(1) * scale / 2);
        int direction = 0; // 0: right, 1: down, 2: left, 3: up
        
        // Draw squares for each Fibonacci number (skip F(0))
        for (int i = 1; i < numbers.size(); i++) {
            long size = numbers.get(i);
            double squareSize = size * scale;
            
            // Set color with cycling through the color array
            gc.setFill(colors[i % colors.length]);
            gc.fillRect(x, y, squareSize, squareSize);
            
            // Draw border
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeRect(x, y, squareSize, squareSize);
            
            // Add number label
            gc.setFill(Color.BLACK);
            gc.fillText("F(" + i + ") = " + size, x + squareSize/2 - 20, y + squareSize/2);
            
            // Update position for next square based on direction
            if (i + 1 < numbers.size()) {
                switch (direction) {
                    case 0: // right
                        x += squareSize;
                        y -= numbers.get(i+1) * scale;
                        break;
                    case 1: // down
                        y += squareSize;
                        x -= numbers.get(i+1) * scale;
                        break;
                    case 2: // left
                        x -= numbers.get(i+1) * scale;
                        break;
                    case 3: // up
                        y -= numbers.get(i+1) * scale;
                        break;
                }
                
                // Update direction (clockwise)
                direction = (direction + 1) % 4;
            }
        }
        
        // Draw spiral for the current subset of numbers
        drawFibonacciSpiral(numbers, scale, centerX, centerY);
    }

    private void drawFibonacciSpiral(List<Long> numbers, double scale, double centerX, double centerY) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        double x = centerX - (numbers.get(1) * scale / 2);
        double y = centerY - (numbers.get(1) * scale / 2);
        int direction = 0; // 0: right, 1: down, 2: left, 3: up
        
        for (int i = 1; i < numbers.size(); i++) {
            long size = numbers.get(i);
            double arcSize = size * scale;
            
            // Draw quarter circle arc based on direction
            switch (direction) {
                case 0: // right
                    gc.strokeArc(x, y - arcSize, arcSize * 2, arcSize * 2, 180, -90, ArcType.OPEN);
                    x += arcSize;
                    y -= numbers.get(i+1) * scale;
                    break;
                case 1: // down
                    gc.strokeArc(x - arcSize, y, arcSize * 2, arcSize * 2, 90, -90, ArcType.OPEN);
                    y += arcSize;
                    x -= numbers.get(i+1) * scale;
                    break;
                case 2: // left
                    gc.strokeArc(x - arcSize, y - arcSize, arcSize * 2, arcSize * 2, 0, -90, ArcType.OPEN);
                    x -= arcSize;
                    break;
                case 3: // up
                    gc.strokeArc(x, y - arcSize, arcSize * 2, arcSize * 2, 270, -90, ArcType.OPEN);
                    y -= arcSize;
                    break;
            }
            
            // Update direction (clockwise)
            direction = (direction + 1) % 4;
            
            // Break if we're at the last number to avoid IndexOutOfBoundsException
            if (i + 1 >= numbers.size()) break;
        }
    }
    

    private void resetVisualization() {
        // Stop any running animation
        if (animation != null) {
            animation.stop();
        }
        
        // Clear canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Clear results
        resultContainer.getChildren().clear();
        
        // Reset steps label
        stepsLabel.setText("Recursive calls: 0");
        
        // Reset data
        fibonacciNumbers = null;
        recursionCalls = 0;
    }

    private long fibonacciRecursive(int n) {
        recursionCalls++; // Count the number of recursive calls
        
        if (n <= 1) {
            return n;
        }
        
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }    

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
