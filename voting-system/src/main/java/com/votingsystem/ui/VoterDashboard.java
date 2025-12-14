package com.votingsystem.ui;

import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.service.VotingService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Voter Dashboard - Professional UI for voters
 */
@Component
public class VoterDashboard {
    
    @Autowired
    private VotingService votingService;
    
    private Scene scene;
    private String username;
    private String token;
    private VBox mainContent;
    private Map<String, Object> dashboardData;
    
    public void initialize(String username, String token) {
        this.username = username;
        this.token = token;
        loadDashboardData();
    }
    
    public Scene getScene() {
        if (scene == null) {
            scene = createDashboardScene();
        }
        return scene;
    }
    
    private void loadDashboardData() {
        new Thread(() -> {
            dashboardData = votingService.getVoterDashboard(username);
            Platform.runLater(this::refreshContent);
        }).start();
    }
    
    private Scene createDashboardScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dashboard-container");
        root.setStyle("-fx-background-color: #F4F6F9;");
        
        // Top navigation
        HBox topNav = createTopNav();
        root.setTop(topNav);
        
        // Main content area
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
        
        Scene dashboardScene = new Scene(root, 1200, 750);
        dashboardScene.getStylesheets().add(
            getClass().getResource("/css/voting-system.css").toExternalForm()
        );
        
        return dashboardScene;
    }
    
    private HBox createTopNav() {
        HBox nav = new HBox();
        nav.getStyleClass().add("top-nav");
        nav.setPadding(new Insets(16, 32, 16, 32));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color: #0A1F44;");
        
        Label title = new Label("🗳️ Secure Voting System");
        title.getStyleClass().add("nav-title");
        title.setStyle(
            "-fx-text-fill: white; " +
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label usernameLabel = new Label("Welcome, " + username);
        usernameLabel.getStyleClass().add("nav-username");
        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-secondary");
        logoutBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 4px; " +
            "-fx-background-radius: 4px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> handleLogout());
        
        nav.getChildren().addAll(title, spacer, usernameLabel, new Label("  "), logoutBtn);
        return nav;
    }
    
    private void refreshContent() {
        mainContent.getChildren().clear();
        
        if (dashboardData == null) {
            mainContent.getChildren().add(new Label("Loading..."));
            return;
        }
        
        // Voter status card
        VBox statusCard = createStatusCard();
        
        // Active elections section
        VBox electionsSection = createElectionsSection();
        
        mainContent.getChildren().addAll(statusCard, electionsSection);
    }
    
    private VBox createStatusCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        
        Label header = new Label("Voter Status");
        header.getStyleClass().add("card-header");
        header.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(12);
        
        addInfoRow(infoGrid, 0, "Voter Name:", (String) dashboardData.get("voterName"));
        addInfoRow(infoGrid, 1, "Voter ID:", (String) dashboardData.get("voterId"));
        
        Boolean verified = (Boolean) dashboardData.get("verified");
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #2E2E2E;");
        
        Label statusBadge = new Label(verified ? "✓ Verified" : "⏳ Pending Verification");
        statusBadge.getStyleClass().add(verified ? "status-verified" : "status-pending");
        statusBadge.setStyle(
            "-fx-padding: 6px 12px; " +
            "-fx-background-radius: 12px; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: 600; " +
            (verified ? 
                "-fx-background-color: rgba(46, 204, 113, 0.15); -fx-text-fill: #2ECC71;" :
                "-fx-background-color: rgba(243, 156, 18, 0.15); -fx-text-fill: #F39C12;")
        );
        
        statusBox.getChildren().addAll(statusLabel, statusBadge);
        infoGrid.add(statusBox, 0, 2, 2, 1);
        
        card.getChildren().addAll(header, infoGrid);
        return card;
    }
    
    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-weight: 600; -fx-text-fill: #2E2E2E;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: #6B7280;");
        
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }
    
    @SuppressWarnings("unchecked")
    private VBox createElectionsSection() {
        VBox section = new VBox(20);
        
        Label header = new Label("Active Elections");
        header.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        List<Election> elections = (List<Election>) dashboardData.get("activeElections");
        
        if (elections == null || elections.isEmpty()) {
            Label noElections = new Label("No active elections at the moment");
            noElections.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
            section.getChildren().addAll(header, noElections);
            return section;
        }
        
        VBox electionsList = new VBox(15);
        for (Election election : elections) {
            VBox electionCard = createElectionCard(election);
            electionsList.getChildren().add(electionCard);
        }
        
        section.getChildren().addAll(header, electionsList);
        return section;
    }
    
    private VBox createElectionCard(Election election) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        
        Label electionName = new Label(election.getName());
        electionName.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        Label electionDesc = new Label(election.getDescription());
        electionDesc.setStyle("-fx-text-fill: #6B7280; -fx-wrap-text: true;");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        Label timeInfo = new Label(
            "📅 " + election.getStartTime().format(formatter) + 
            " - " + election.getEndTime().format(formatter)
        );
        timeInfo.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");
        
        // Check if voted
        boolean hasVoted = votingService.hasVoted(username, election.getId());
        
        Button voteButton = new Button(hasVoted ? "✓ Already Voted" : "View Candidates & Vote");
        voteButton.setDisable(hasVoted);
        voteButton.getStyleClass().add(hasVoted ? "button-success" : "button-primary");
        voteButton.setStyle(
            (hasVoted ? 
                "-fx-background-color: #2ECC71; " :
                "-fx-background-color: #1F4FD8; ") +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 600; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: " + (hasVoted ? "default" : "hand") + ";"
        );
        
        if (!hasVoted) {
            voteButton.setOnAction(e -> showCandidates(election));
        }
        
        card.getChildren().addAll(electionName, electionDesc, timeInfo, voteButton);
        return card;
    }
    
    private void showCandidates(Election election) {
        // Create modal for candidates
        Stage candidateStage = new Stage();
        candidateStage.setTitle("Select Candidate - " + election.getName());
        
        VBox modalContent = new VBox(20);
        modalContent.setPadding(new Insets(30));
        modalContent.setStyle("-fx-background-color: #F4F6F9;");
        
        Label title = new Label("Select Your Candidate");
        title.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        List<Candidate> candidates = votingService.getCandidatesByElection(election.getId());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox candidatesList = new VBox(15);
        for (Candidate candidate : candidates) {
            HBox candidateCard = createCandidateCard(candidate, election, candidateStage);
            candidatesList.getChildren().add(candidateCard);
        }
        
        scrollPane.setContent(candidatesList);
        modalContent.getChildren().addAll(title, scrollPane);
        
        Scene modalScene = new Scene(modalContent, 700, 600);
        modalScene.getStylesheets().add(
            getClass().getResource("/css/voting-system.css").toExternalForm()
        );
        candidateStage.setScene(modalScene);
        candidateStage.show();
    }
    
    private HBox createCandidateCard(Candidate candidate, Election election, Stage stage) {
        HBox card = new HBox(20);
        card.getStyleClass().add("candidate-card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: #D1D5DB; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 8px;"
        );
        
        VBox infoBox = new VBox(8);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        Label name = new Label(candidate.getName());
        name.getStyleClass().add("candidate-name");
        name.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        Label party = new Label(candidate.getPartyName());
        party.getStyleClass().add("candidate-party");
        party.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
        
        infoBox.getChildren().addAll(name, party);
        
        Button voteBtn = new Button("Vote");
        voteBtn.getStyleClass().add("button-primary");
        voteBtn.setStyle(
            "-fx-background-color: #1F4FD8; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: 600; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        voteBtn.setOnAction(e -> confirmVote(candidate, election, stage));
        
        card.getChildren().addAll(infoBox, voteBtn);
        return card;
    }
    
    private void confirmVote(Candidate candidate, Election election, Stage candidateStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Your Vote");
        alert.setHeaderText("Are you sure you want to vote for:");
        alert.setContentText(
            candidate.getName() + " (" + candidate.getPartyName() + ")\n\n" +
            "⚠️ Warning: Once submitted, your vote cannot be changed!"
        );
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                castVote(candidate.getId(), election.getId(), candidateStage);
            }
        });
    }
    
    private void castVote(Long candidateId, Long electionId, Stage candidateStage) {
        new Thread(() -> {
            Map<String, Object> response = votingService.castVote(
                username, electionId, candidateId, 
                "127.0.0.1", "JavaFX", "session-" + System.currentTimeMillis()
            );
            
            Platform.runLater(() -> {
                if ((Boolean) response.get("success")) {
                    candidateStage.close();
                    showSuccessAlert((String) response.get("message"));
                    loadDashboardData(); // Refresh
                } else {
                    showErrorAlert((String) response.get("message"));
                }
            });
        }).start();
    }
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Vote Recorded Successfully");
        alert.setContentText("✓ " + message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Vote Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void handleLogout() {
        VotingSystemApplication.showLoginScreen();
    }
}
