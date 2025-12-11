package com.ucop;

import com.ucop.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;

import java.net.URL;

public class MainApp extends Application {

    private Stage primaryStage;
    private SessionFactory sessionFactory;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("UCOP - Universal Commerce & Operations Platform");

        try {
            System.out.println("=========== UCOP STARTUP ===========");

            // ---------------------------------------
            // INIT HIBERNATE
            // ---------------------------------------
            System.out.println("Initializing Hibernate...");
            sessionFactory = HibernateUtil.getSessionFactory();
            System.out.println("✓ Database connection established!");


            // ---------------------------------------
            // LOAD CUSTOMER LOGIN FXML
            // ---------------------------------------
            String fxmlPath = "/UI/customer/customer_login.fxml";
            System.out.println("Loading FXML: " + fxmlPath);

            URL url = getClass().getResource(fxmlPath);
            System.out.println("Resolved FXML URL = " + url);

            if (url == null) {
                throw new RuntimeException(
                    "❌ FXML NOT FOUND at: " + fxmlPath +
                    "\n⚠ Kiểm tra lại:" +
                    "\n1️⃣ Thư mục: src/main/resources/UI/customer/" +
                    "\n2️⃣ File: customer_login.fxml" +
                    "\n3️⃣ Maven có copy resource chưa"
                );
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // ---------------------------------------
            // SHOW UI
            // ---------------------------------------
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

            System.out.println("✓ UCOP JavaFX UI Loaded Successfully!");
            System.out.println("======================================");

        } catch (Exception e) {

            System.err.println("✗ ERROR starting JavaFX application:");
            e.printStackTrace();

            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);

            alert.setTitle("Startup Error");
            alert.setHeaderText("Unable to start UCOP Application");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void stop() {
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
