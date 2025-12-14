package com.votingsystem.ui;

import com.votingsystem.model.*;
import com.votingsystem.service.AdminService;
import com.votingsystem.service.AuditLogService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * Admin Dashboard - Comprehensive admin panel with sidebar navigation
 */
@Component
public class AdminDashboard {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AuditLogService auditLogService;
    
    private Scene scene;
    private String username;
    private String token;
    private BorderPane mainLayout;
    private StackPane contentArea;
    private Map<String, Object> dashboardStats;
    
    public void initialize(String username, String token) {
        this.username = username;
        this.token = token;
        loadDashboardStats();
    }
    
    public Scene getScene() {
        if (scene == null) {
            scene = createAdminScene();
        }
        return scene;
    }
    
    private void loadDashboardStats() {
        new Thread(() -> {
            dashboardStats = adminService.getDashboardStats();
            Platform.runLater(this::showDashboardView);
        }).start();
    }
    
    private Scene createAdminScene() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #F4F6F9;");
        
        // Top navigation
        HBox topNav = createTopNav();
        mainLayout.setTop(topNav);
        
        // Sidebar
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);
        
        // Content area
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));
        mainLayout.setCenter(contentArea);
        
        Scene adminScene = new Scene(mainLayout, 1400, 800);
        adminScene.getStylesheets().add(
            getClass().getResource("/css/voting-system.css").toExternalForm()
        );
        
        return adminScene;
    }
    
    private HBox createTopNav() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 32, 16, 32));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color: #0A1F44;");
        
        Label title = new Label("🛡️ Admin Panel - Voting System");
        title.setStyle(
            "-fx-text-fill: white; " +
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label adminLabel = new Label("Admin: " + username);
        adminLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Button logoutBtn = new Button("Logout");
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
        logoutBtn.setOnAction(e -> VotingSystemApplication.showLoginScreen());
        
        nav.getChildren().addAll(title, spacer, adminLabel, new Label("  "), logoutBtn);
        return nav;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setStyle("-fx-background-color: #0A1F44; -fx-min-width: 250px;");
        
        Button dashboardBtn = createSidebarButton("📊 Dashboard", true);
        dashboardBtn.setOnAction(e -> showDashboardView());
        
        Button electionsBtn = createSidebarButton("🗳️ Manage Elections", false);
        electionsBtn.setOnAction(e -> showElectionsView());
        
        Button candidatesBtn = createSidebarButton("👤 Manage Candidates", false);
        candidatesBtn.setOnAction(e -> showCandidatesView());
        
        Button votersBtn = createSidebarButton("👥 Manage Voters", false);
        votersBtn.setOnAction(e -> showVotersView());
        
        Button resultsBtn = createSidebarButton("📈 Results", false);
        resultsBtn.setOnAction(e -> showResultsView());
        
        Button logsBtn = createSidebarButton("🔐 Security Logs", false);
        logsBtn.setOnAction(e -> showLogsView());
        
        sidebar.getChildren().addAll(
            dashboardBtn, electionsBtn, candidatesBtn, 
            votersBtn, resultsBtn, logsBtn
        );
        
        return sidebar;
    }
    
    private Button createSidebarButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.getStyleClass().add("sidebar-item");
        btn.setStyle(
            "-fx-background-color: " + (active ? "#1F4FD8" : "transparent") + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 16px 24px; " +
            "-fx-cursor: hand; " +
            "-fx-border-width: 0;"
        );
        
        btn.setOnMouseEntered(e -> {
            if (!active) {
                btn.setStyle(btn.getStyle() + "-fx-background-color: rgba(255,255,255,0.1);");
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (!active) {
                btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(255,255,255,0.1);", ""));
            }
        });
        
        return btn;
    }
    
    private void showDashboardView() {
        VBox content = new VBox(25);
        content.setAlignment(Pos.TOP_LEFT);
        
        Label title = new Label("Dashboard Overview");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        // Stats cards
        GridPane statsGrid = createStatsGrid();
        
        content.getChildren().addAll(title, statsGrid);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }
    
    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        if (dashboardStats != null) {
            grid.add(createStatCard("Total Voters", dashboardStats.get("totalVoters").toString(), "#1F4FD8"), 0, 0);
            grid.add(createStatCard("Verified Voters", dashboardStats.get("verifiedVoters").toString(), "#2ECC71"), 1, 0);
            grid.add(createStatCard("Pending Voters", dashboardStats.get("pendingVoters").toString(), "#F39C12"), 2, 0);
            grid.add(createStatCard("Total Elections", dashboardStats.get("totalElections").toString(), "#0A1F44"), 0, 1);
            grid.add(createStatCard("Active Elections", dashboardStats.get("activeElections").toString(), "#2ECC71"), 1, 1);
            grid.add(createStatCard("Total Votes Cast", dashboardStats.get("totalVotes").toString(), "#1F4FD8"), 2, 1);
        }
        
        return grid;
    }
    
    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(24));
        card.setMinWidth(200);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + ";"
        );
        
        Label labelText = new Label(label);
        labelText.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #6B7280;"
        );
        
        card.getChildren().addAll(valueLabel, labelText);
        return card;
    }
    
    private void showElectionsView() {
        VBox content = new VBox(20);
        
        Label title = new Label("Manage Elections");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        Button addButton = new Button("+ Add New Election");
        addButton.setStyle(
            "-fx-background-color: #1F4FD8; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: 600; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        addButton.setOnAction(e -> showAddElectionDialog());
        
        TableView<Election> table = createElectionsTable();
        
        content.getChildren().addAll(title, addButton, table);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }
    
    private TableView<Election> createElectionsTable() {
        TableView<Election> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #D1D5DB; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px;"
        );
        
        TableColumn<Election, String> nameCol = new TableColumn<>("Election Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(200);
        
        TableColumn<Election, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        TableColumn<Election, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Election, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            
            {
                editBtn.setStyle(
                    "-fx-background-color: #1F4FD8; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 6px 12px; " +
                    "-fx-background-radius: 4px; " +
                    "-fx-cursor: hand;"
                );
                
                deleteBtn.setStyle(
                    "-fx-background-color: #E74C3C; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 6px 12px; " +
                    "-fx-background-radius: 4px; " +
                    "-fx-cursor: hand;"
                );
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        
        table.getColumns().addAll(nameCol, typeCol, statusCol, actionsCol);
        
        // Load data
        new Thread(() -> {
            List<Election> elections = adminService.getAllElections();
            Platform.runLater(() -> {
                ObservableList<Election> data = FXCollections.observableArrayList(elections);
                table.setItems(data);
            });
        }).start();
        
        return table;
    }
    
    private void showAddElectionDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Election");
        alert.setHeaderText("Add New Election");
        alert.setContentText("Election creation form will be implemented here.");
        alert.showAndWait();
    }
    
    private void showCandidatesView() {
        showPlaceholder("Manage Candidates", "Candidate management interface");
    }
    
    private void showVotersView() {
        VBox content = new VBox(20);
        
        Label title = new Label("Manage Voters");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        TableView<Voter> table = createVotersTable();
        
        content.getChildren().addAll(title, table);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }
    
    private TableView<Voter> createVotersTable() {
        TableView<Voter> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #D1D5DB; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px;"
        );
        
        TableColumn<Voter, String> idCol = new TableColumn<>("Voter ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("voterId"));
        
        TableColumn<Voter, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setMinWidth(200);
        
        TableColumn<Voter, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Voter, Boolean> verifiedCol = new TableColumn<>("Verified");
        verifiedCol.setCellValueFactory(new PropertyValueFactory<>("verified"));
        
        table.getColumns().addAll(idCol, nameCol, statusCol, verifiedCol);
        
        // Load data
        new Thread(() -> {
            List<Voter> voters = adminService.getAllVoters();
            Platform.runLater(() -> {
                ObservableList<Voter> data = FXCollections.observableArrayList(voters);
                table.setItems(data);
            });
        }).start();
        
        return table;
    }
    
    private void showResultsView() {
        showPlaceholder("Election Results", "Results visualization with charts");
    }
    
    private void showLogsView() {
        VBox content = new VBox(20);
        
        Label title = new Label("Security Audit Logs");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        TableView<AuditLog> table = createLogsTable();
        
        content.getChildren().addAll(title, table);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }
    
    private TableView<AuditLog> createLogsTable() {
        TableView<AuditLog> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #D1D5DB; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px;"
        );
        
        TableColumn<AuditLog, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setMinWidth(200);
        
        TableColumn<AuditLog, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        
        TableColumn<AuditLog, String> severityCol = new TableColumn<>("Severity");
        severityCol.setCellValueFactory(new PropertyValueFactory<>("severity"));
        
        TableColumn<AuditLog, String> ipCol = new TableColumn<>("IP Address");
        ipCol.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        
        table.getColumns().addAll(actionCol, typeCol, severityCol, ipCol);
        
        // Load data
        new Thread(() -> {
            List<AuditLog> logs = auditLogService.getRecentLogs(0, 50).getContent();
            Platform.runLater(() -> {
                ObservableList<AuditLog> data = FXCollections.observableArrayList(logs);
                table.setItems(data);
            });
        }).start();
        
        return table;
    }
    
    private void showPlaceholder(String titleText, String description) {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        
        Label title = new Label(titleText);
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0A1F44;"
        );
        
        Label desc = new Label(description);
        desc.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
        
        content.getChildren().addAll(title, desc);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }
}
