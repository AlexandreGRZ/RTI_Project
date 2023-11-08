package com.hepl.purchaseclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PurchaseClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PurchaseClientApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 780, 620);
        stage.setTitle("Le Maraicher en ligne");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}