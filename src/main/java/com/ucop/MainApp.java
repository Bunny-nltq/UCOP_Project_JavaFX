package com.ucop;

import com.ucop.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;

/**
 * Main Application Class
 */
public class MainApp extends Application {
    
    private Stage primaryStage;
    private SessionFactory sessionFactory;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("UCOP - Universal Commerce & Operations Platform");
        
        try {
            // Initialize Hibernate
            System.out.println("Initializing Hibernate...");
            sessionFactory = HibernateUtil.getSessionFactory();
            System.out.println("✓ Database connection established!");
            
            // Load FXML - CUSTOMER UI
            System.out.println("Loading Customer UI...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/customer/customer-main.fxml"));
            Parent root = loader.load();
            
            // Create scene
            Scene scene = new Scene(root, 1400, 900);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
            System.out.println("✓ Customer Application started successfully!");
            
        } catch (Exception e) {
            System.err.println("✗ Error starting application:");
            e.printStackTrace();
            
            // Show error dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Lỗi Khởi Động");
            alert.setHeaderText("Không thể khởi động ứng dụng");
            alert.setContentText("Lỗi: " + e.getMessage() + "\n\nVui lòng kiểm tra:\n" +
                    "1. Kết nối database MySQL (localhost:3307)\n" +
                    "2. Database 'ucop_project_javafx' đã được tạo\n" +
                    "3. Username/password: root/123456");
            alert.showAndWait();
        }
    }
    
    /**
     * Returns the main stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    @Override
    public void stop() {
        // Cleanup resources
        System.out.println("Shutting down application...");
        if (sessionFactory != null) {
            HibernateUtil.shutdown();
        }
        System.out.println("✓ Application closed successfully!");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

