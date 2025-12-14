package com.votingsystem.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Application Class - Entry point for the Voting System
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.votingsystem")
@EntityScan(basePackages = "com.votingsystem.model")
@EnableJpaRepositories(basePackages = "com.votingsystem.repository")
public class VotingSystemApplication extends Application {
    
    private static ConfigurableApplicationContext springContext;
    private static Stage primaryStageRef;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void init() {
        // Initialize Spring context
        springContext = SpringApplication.run(VotingSystemApplication.class);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStageRef = primaryStage;
        primaryStage.setTitle("Secure Online Voting System");
        primaryStage.setResizable(false);
        
        // Show login screen
        showLoginScreen();
        
        primaryStage.show();
    }
    
    public static void showLoginScreen() {
        LoginScreen loginScreen = springContext.getBean(LoginScreen.class);
        Scene scene = loginScreen.getScene();
        primaryStageRef.setScene(scene);
        primaryStageRef.centerOnScreen();
    }
    
    public static void showVoterDashboard(String username, String token) {
        VoterDashboard dashboard = springContext.getBean(VoterDashboard.class);
        dashboard.initialize(username, token);
        Scene scene = dashboard.getScene();
        primaryStageRef.setScene(scene);
        primaryStageRef.centerOnScreen();
    }
    
    public static void showAdminDashboard(String username, String token) {
        AdminDashboard dashboard = springContext.getBean(AdminDashboard.class);
        dashboard.initialize(username, token);
        Scene scene = dashboard.getScene();
        primaryStageRef.setScene(scene);
        primaryStageRef.centerOnScreen();
    }
    
    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }
    
    @Override
    public void stop() {
        springContext.close();
    }
}
