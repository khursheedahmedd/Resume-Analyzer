package com.resumeanalyzer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;
import java.io.IOException;

public class ResumeAnalyzerApp extends Application {

    private File selectedFile;
    private ComboBox<String> jobRoleComboBox;
    private TextArea resultArea;
    private static final String PRIMARY_COLOR = "#2196F3";
    private static final String SECONDARY_COLOR = "#64B5F6";
    private static final String SUCCESS_COLOR = "#4CAF50";
    private static final String BACKGROUND_COLOR = "#F5F5F5";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Resume Analyzer");

        // Create main layout with styling
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Header
        Label headerLabel = new Label("Resume Analyzer");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headerLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        // File selection with improved styling
        VBox fileSection = new VBox(10);
        fileSection.setAlignment(Pos.CENTER_LEFT);
        Label fileHeaderLabel = new Label("Upload Resume");
        fileHeaderLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        
        HBox fileSelectionBox = new HBox(15);
        Button selectFileButton = new Button("Choose File");
        styleButton(selectFileButton, PRIMARY_COLOR);
        Label fileLabel = new Label("No file selected");
        fileLabel.setStyle("-fx-text-fill: #757575;");
        fileSelectionBox.getChildren().addAll(selectFileButton, fileLabel);
        fileSelectionBox.setAlignment(Pos.CENTER_LEFT);
        
        fileSection.getChildren().addAll(fileHeaderLabel, fileSelectionBox);

        // Job role selection with improved styling
        VBox jobSection = new VBox(10);
        jobSection.setAlignment(Pos.CENTER_LEFT);
        Label jobHeaderLabel = new Label("Select Job Role");
        jobHeaderLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        
        jobRoleComboBox = new ComboBox<>();
        jobRoleComboBox.getItems().addAll(
            "Frontend Developer",
            "Backend Developer",
            "Full Stack Developer",
            "Data Analyst",
            "Machine Learning Engineer",
            "DevOps Engineer",
            "UI/UX Designer"
        );
        jobRoleComboBox.setValue("Frontend Developer");
        jobRoleComboBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + PRIMARY_COLOR + ";" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 5;"
        );
        jobRoleComboBox.setPrefWidth(200);
        
        jobSection.getChildren().addAll(jobHeaderLabel, jobRoleComboBox);

        // Analyze button with improved styling
        Button analyzeButton = new Button("Analyze Resume");
        styleButton(analyzeButton, SUCCESS_COLOR);
        analyzeButton.setPrefWidth(200);

        // Results area with improved styling
        VBox resultSection = new VBox(10);
        resultSection.setAlignment(Pos.CENTER_LEFT);
        Label resultHeaderLabel = new Label("Analysis Results");
        resultHeaderLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(12);
        resultArea.setWrapText(true);
        resultArea.setStyle(
            "-fx-control-inner-background: white;" +
            "-fx-border-color: " + SECONDARY_COLOR + ";" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;" +
            "-fx-padding: 10;"
        );
        
        resultSection.getChildren().addAll(resultHeaderLabel, resultArea);

        // Add components to main layout with spacing
        mainLayout.getChildren().addAll(
            headerLabel,
            createSeparator(),
            fileSection,
            createSeparator(),
            jobSection,
            analyzeButton,
            createSeparator(),
            resultSection
        );

        // Set up event handlers
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resume Files", "*.pdf", "*.docx")
            );
            selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileLabel.setText(selectedFile.getName());
                fileLabel.setStyle("-fx-text-fill: " + SUCCESS_COLOR + ";");
            }
        });

        analyzeButton.setOnAction(e -> {
            if (selectedFile == null) {
                showAlert("Please select a resume file first.");
                return;
            }

            try {
                // Extract text from resume
                String resumeText = ResumeParser.extractText(selectedFile);
                
                // Analyze resume
                String selectedRole = jobRoleComboBox.getValue();
                ResumeAnalyzer.AnalysisResult result = ResumeAnalyzer.analyzeResume(resumeText, selectedRole);
                
                // Display results with improved formatting
                StringBuilder output = new StringBuilder();
                output.append(String.format("📄 Resume Analysis for %s\n\n", selectedRole));
                output.append(String.format("Overall Match Score: %.1f%%\n", result.getScore()));
                output.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                
                output.append("✅ Found Keywords:\n");
                for (String keyword : result.getFoundKeywords()) {
                    output.append("  • ").append(keyword).append("\n");
                }
                
                output.append("\n💡 Improvement Suggestions:\n");
                for (String suggestion : result.getSuggestions()) {
                    output.append("  • ").append(suggestion).append("\n");
                }
                
                resultArea.setText(output.toString());
                
            } catch (IOException ex) {
                showAlert("Error reading file: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                showAlert(ex.getMessage());
            }
        });

        // Create scene with responsive width
        Scene scene = new Scene(mainLayout, 700, 800);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 4;"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: derive(" + color + ", 20%);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 4;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 4;"
            )
        );
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");
        return separator;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 