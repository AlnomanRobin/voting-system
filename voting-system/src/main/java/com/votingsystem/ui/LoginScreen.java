package com.votingsystem.ui;

import com.votingsystem.service.AuthService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Login Screen with Professional Government-Grade UI
 */
@Component
public class LoginScreen {
    
    @Autowired
    private AuthService authService;
    
    private Scene scene;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    private Button loginButton;
    private ProgressIndicator progressIndicator;
    
    public Scene getScene() {
        if (scene == null) {
            scene = createLoginScene();
        }
        return scene;
    }
    
    private Scene createLoginScene() {
        // Main container
        StackPane root = new StackPane();
        root.getStyleClass().add("login-container");
        root.setStyle("-fx-background-color: #F4F6F9;");
        
        // Login card
        VBox loginCard = createLoginCard();
        
        root.getChildren().add(loginCard);
        
        Scene loginScene = new Scene(root, 1000, 650);
        loginScene.getStylesheets().add(
            getClass().getResource("/css/voting-system.css").toExternalForm()
        );
        
        return loginScene;
    }
    
    private VBox createLoginCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 4);"
        );
        
        // Logo and title
        VBox header = createHeader();
        
        // Username field
        VBox usernameBox = createInputField("Voter ID / Username", true);
        
        // Password field
        VBox passwordBox = createInputField("Password", false);
        
        // Error label
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        // Login button
        loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("button-primary");
        loginButton.setStyle(
            "-fx-background-color: #1F4FD8; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 14px 24px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());
        
        // Progress indicator
        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(30, 30);
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        
        // Security notice
        Label securityNotice = new Label("🔒 Secure connection with encryption");
        securityNotice.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-text-fill: #6B7280; " +
            "-fx-padding: 10px 0 0 0;"
        );
        
        // Session timeout notice
        Label sessionNotice = new Label("Session timeout: 30 minutes");
        sessionNotice.setStyle(
            "-fx-font-size: 10px; " +
            "-fx-text-fill: #9CA3AF;"
        );
        
        card.getChildren().addAll(
            header,
            usernameBox,
            passwordBox,
            errorLabel,
            loginButton,
            progressIndicator,
            securityNotice,
            sessionNotice
        );
        
        // Enable Enter key to login
        passwordField.setOnAction(e -> handleLogin());
        
        return card;
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        // Logo/Title
        Label logo = new Label("🗳️");
        logo.setStyle("-fx-font-size: 48px;");
        
        Label title = new Label("Secure Voting System");
        title.getStyleClass().add("login-header");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        Label subtitle = new Label("Government-Grade Security");
        subtitle.getStyleClass().add("login-subtitle");
        subtitle.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #6B7280;"
        );
        
        header.getChildren().addAll(logo, title, subtitle);
        return header;
    }
    
    private VBox createInputField(String labelText, boolean isTextField) {
        VBox fieldBox = new VBox(8);
        
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        label.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: 600; " +
            "-fx-text-fill: #2E2E2E;"
        );
        
        if (isTextField) {
            usernameField = new TextField();
            usernameField.setPromptText("Enter your Voter ID or username");
            usernameField.getStyleClass().add("text-field");
            usernameField.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #D1D5DB; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 4px; " +
                "-fx-background-radius: 4px; " +
                "-fx-padding: 12px; " +
                "-fx-font-size: 14px;"
            );
            fieldBox.getChildren().addAll(label, usernameField);
        } else {
            passwordField = new PasswordField();
            passwordField.setPromptText("Enter your password");
            passwordField.getStyleClass().add("password-field");
            passwordField.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #D1D5DB; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 4px; " +
                "-fx-background-radius: 4px; " +
                "-fx-padding: 12px; " +
                "-fx-font-size: 14px;"
            );
            fieldBox.getChildren().addAll(label, passwordField);
        }
        
        return fieldBox;
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        
        // Show loading
        loginButton.setDisable(true);
        progressIndicator.setVisible(true);
        progressIndicator.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        // Perform login in background thread
        new Thread(() -> {
            try {
                Map<String, Object> response = authService.login(username, password, "127.0.0.1");
                
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    
                    if ((Boolean) response.get("success")) {
                        String role = (String) response.get("role");
                        String token = (String) response.get("token");
                        
                        if ("VOTER".equals(role)) {
                            VotingSystemApplication.showVoterDashboard(username, token);
                        } else if ("ADMIN".equals(role) || "SUPER_ADMIN".equals(role)) {
                            VotingSystemApplication.showAdminDashboard(username, token);
                        }
                    } else {
                        showError((String) response.get("message"));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    showError("Login failed. Please try again.");
                });
            }
        }).start();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        
        // Shake animation for error
        usernameField.setStyle(usernameField.getStyle() + "-fx-border-color: #E74C3C;");
        passwordField.setStyle(passwordField.getStyle() + "-fx-border-color: #E74C3C;");
        
        // Reset border color after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    usernameField.setStyle(usernameField.getStyle().replace("-fx-border-color: #E74C3C;", ""));
                    passwordField.setStyle(passwordField.getStyle().replace("-fx-border-color: #E74C3C;", ""));
                });
            } catch (InterruptedException ignored) {}
        }).start();
    }
}
