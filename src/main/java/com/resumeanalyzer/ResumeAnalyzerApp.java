package com.resumeanalyzer;

import com.resumeanalyzer.config.ConfigManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Node;
import java.io.File;
import java.io.IOException;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import javafx.scene.web.WebView;

public class ResumeAnalyzerApp extends Application {

    private File selectedFile;
    private ComboBox<String> jobRoleComboBox;
    private WebView resultWebView;
    private CheckBox aiAnalysisCheckBox;
    private GroqAnalyzer groqAnalyzer;
    private String groqApiKey;
    private static final String PRIMARY_COLOR = "#2196F3";
    private static final String SECONDARY_COLOR = "#64B5F6";
    private static final String SUCCESS_COLOR = "#4CAF50";
    private static final String BACKGROUND_COLOR = "#F5F5F5";

    @Override
    public void start(Stage primaryStage) {
        // Try to get API key from environment variable
        initializeGroqAnalyzer();

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

        // Add AI Analysis checkbox (no configure button)
        HBox aiSection = new HBox(15);
        aiSection.setAlignment(Pos.CENTER_LEFT);
        aiAnalysisCheckBox = new CheckBox("Include AI-Powered Analysis");
        aiAnalysisCheckBox.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        aiSection.getChildren().addAll(aiAnalysisCheckBox);
        updateAiControlsState();

        // Analyze button with improved styling
        Button analyzeButton = new Button("Analyze Resume");
        styleButton(analyzeButton, SUCCESS_COLOR);
        analyzeButton.setPrefWidth(200);

        // Results area with improved styling
        VBox resultSection = new VBox(10);
        resultSection.setAlignment(Pos.CENTER_LEFT);
        Label resultHeaderLabel = new Label("Analysis Results");
        resultHeaderLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        
        resultWebView = new WebView();
        resultWebView.setPrefHeight(400);
        resultWebView.setStyle(
            "-fx-border-color: " + SECONDARY_COLOR + ";" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;"
        );
        
        resultSection.getChildren().addAll(resultHeaderLabel, resultWebView);

        // Add components to main layout with spacing
        mainLayout.getChildren().clear();
        mainLayout.getChildren().addAll(
            headerLabel,
            createSeparator(),
            fileSection,
            createSeparator(),
            jobSection,
            aiSection,
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

            // Run analysis in a background thread (no loading dialog)
            new Thread(() -> {
                try {
                    String html;
                    // Extract text from resume
                    String resumeText = ResumeParser.extractText(selectedFile);
                    // Analyze resume
                    String selectedRole = jobRoleComboBox.getValue();
                    ResumeAnalyzer.AnalysisResult result = ResumeAnalyzer.analyzeResume(resumeText, selectedRole);
                    // Display results with improved formatting (markdown)
                    StringBuilder output = new StringBuilder();
                    output.append(String.format("# 📄 Resume Analysis for %s\n\n", selectedRole));
                    output.append(String.format("**Overall Match Score:** %.1f%%\n\n", result.getScore()));
                    output.append("---\n\n");
                    output.append("## ✅ Found Keywords\n");
                    for (String keyword : result.getFoundKeywords()) {
                        output.append("- ").append(keyword).append("\n");
                    }
                    output.append("\n## 💡 Improvement Suggestions\n");
                    for (String suggestion : result.getSuggestions()) {
                        output.append("- ").append(suggestion).append("\n");
                    }
                    // Add AI analysis if enabled
                    if (aiAnalysisCheckBox.isSelected() && groqAnalyzer != null) {
                        output.append("\n---\n\n");
                        output.append("# 🤖 AI-Powered Analysis\n\n");
                        try {
                            String aiAnalysis = groqAnalyzer.analyzeResume(resumeText, selectedRole);
                            output.append(aiAnalysis);
                        } catch (IOException ex) {
                            output.append("⚠️ AI analysis failed: ").append(ex.getMessage()).append("\n");
                        }
                    }
                    // Convert markdown to HTML and display in WebView
                    html = markdownToHtml(output.toString());
                    javafx.application.Platform.runLater(() -> {
                        resultWebView.getEngine().loadContent(html);
                    });
                } catch (IOException | IllegalArgumentException ex) {
                    javafx.application.Platform.runLater(() -> {
                        showAlert("Error: " + ex.getMessage());
                    });
                }
            }).start();
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

    private void initializeGroqAnalyzer() {
        // First try to get API key from environment variable
        groqApiKey = System.getenv("GROQ_API_KEY");
        
        // If not found in environment, try from config file
        if (groqApiKey == null || groqApiKey.isEmpty()) {
            groqApiKey = ConfigManager.getInstance().getGroqApiKey();
        }

        // Initialize analyzer if we have a valid key
        if (groqApiKey != null && !groqApiKey.isEmpty() && !groqApiKey.equals("your_api_key_here")) {
            try {
                groqAnalyzer = new GroqAnalyzer(groqApiKey);
            } catch (IllegalArgumentException e) {
                groqAnalyzer = null;
                groqApiKey = null;
            }
        }
    }

    private void updateAiControlsState() {
        boolean hasApiKey = groqAnalyzer != null;
        aiAnalysisCheckBox.setDisable(!hasApiKey);
        if (!hasApiKey) {
            aiAnalysisCheckBox.setSelected(false);
            aiAnalysisCheckBox.setTooltip(new Tooltip("Configure API key to enable AI analysis"));
        } else {
            aiAnalysisCheckBox.setTooltip(new Tooltip("Use AI to get detailed resume analysis"));
        }
    }

    private String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        org.commonmark.node.Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static void main(String[] args) {
        launch(args);
    }
} 