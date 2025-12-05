package com.ucop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // LOAD ĐÚNG FILE LOGIN CỦA CUSTOMER
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/UI/customer/customer_login.fxml")
        );

        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("UCOP - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
